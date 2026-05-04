package io.github.rikkakawaii0612.mutsumi.numberBomb;

import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.MessageChain;
import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NumberBombService implements Service {
    private Mutsumi mutsumi;
    private final Map<Long, GameInfo> gameInfos = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    @Override
    public void load(String id, ServiceLookup lookup) {
        this.mutsumi = lookup.getMutsumi();
        lookup.getMutsumi().getBotBus().addMessageHandler(this::onHandleMessage);
    }

    @Override
    public void unload() {
    }

    public void onHandleMessage(MutsumiBot bot, Group group, Member sender, Message message) {
        String m = message.asString();
        String str = m.trim();
        if (!str.startsWith("!") && !str.startsWith("！") && !str.startsWith("/")) {
            return;
        }

        // 同步锁, 防止同时操作带来的诡异问题
        synchronized (this.lock) {
            String command = str.substring(1);

            CommandMatcher.Result start = CommandMatchers.BOMB_START.matches(command);
            if (start.doesMatches()) {
                this.commandBombStart(bot, group, sender, start);
                return;
            }

            CommandMatcher.Result select = CommandMatchers.BOMB_SELECT.matches(command);
            if (select.doesMatches()) {
                this.commandBombSelect(bot, group, sender, select);
            }

            CommandMatcher.Result answer = CommandMatchers.BOMB_ANSWER.matches(command);
            if (answer.doesMatches()) {
                this.commandBombAnswer(bot, group, sender);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void commandBombStart(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        long senderId = sender.getId();
        if (this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 当前还有正在进行的数字炸弹游戏。"));
            return;
        }

        Pair<Integer, Integer> pair = params.getValue("range", Pair.class);
        int min = pair.left(), max = pair.right();
        if (min == max) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 最大值等于最小值……这还猜什么啊！"));
            return;
        } else if (min > max) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 最大值比最小值还小……这还猜什么啊！"));
            return;
        }
        if (max > 1000 || min < -1000) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 范围太大了啦！选个阳间点的数字好不好。"));
            return;
        }
        GameInfo gameInfo = new GameInfo(min, max);
        gameInfo.getPlayers().add(senderId);
        this.gameInfos.put(groupId, gameInfo);
        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 已开始新一局的数字炸弹游戏！当前范围：" + min + "~" + max));
    }

    private void commandBombSelect(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        long senderId = sender.getId();
        if (!this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 当前没有正在进行的数字炸弹游戏。"));
            return;
        }

        GameInfo gameInfo = this.gameInfos.get(groupId);
        int value = params.getValue("value", Integer.class);
        if (value < gameInfo.getMin() || value > gameInfo.getMax()) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 超出范围了啊喂！"));
            return;
        }

        boolean newPlayer = !gameInfo.getPlayers().contains(senderId);
        if (gameInfo.select(value, senderId)) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 嘣！恭喜你踩中炸弹了哦。不知道这是幸运还是霉运呢，反正游戏已经结束了。" +
                            "\n炸弹数字：" + value));
            this.gameInfos.remove(groupId);
        } else {
            Message playersInfo = Message.text(newPlayer ? "\n新的旅行伙伴加入！当前轮到：" :
                            (gameInfo.getPlayers().size() > 1 ? "\n当前轮到：" : "\n当前玩家："))
                    .append(getPlayersInfo(gameInfo.getPlayers()));
            int min = gameInfo.getMin(), max = gameInfo.getMax();
            if (max == min) {
                bot.sendMessage(groupId, Message.at(senderId)
                        .append(" 哇哦，你把炸弹排出来了！游戏竟然顺利结束了诶。恭喜。" +
                                "\n炸弹数字：" + min));
                this.gameInfos.remove(groupId);
            } else if (max - min <= 1) {
                bot.sendMessage(groupId, Message.at(senderId)
                        .append(" 还好，没有踩中炸弹……但是接下来二选一，祝你好运吧。" +
                                "\n当前范围：" + min + ", " + max)
                        .append(playersInfo));
            } else if (max - min <= 2) {
                bot.sendMessage(groupId, Message.at(senderId)
                        .append(" 还好，没有踩中炸弹……但是接下来三选二，考验心态的时候到了。" +
                                "\n当前范围：" + min + ", " + (min + 1) + ", " + max)
                        .append(playersInfo));
            } else if (max - min <= 3) {
                bot.sendMessage(groupId, Message.at(senderId)
                        .append(" 还好，没有踩中炸弹……但是空位所剩无几了，你真的能选对吗？" +
                                "\n当前范围：" + min + ", " + (min + 1) + ", " + (min + 2) + ", " + max)
                        .append(playersInfo));
            } else if (max - min <= 9) {
                StringBuilder builder = new StringBuilder();
                builder.append(min);
                for (int i = min + 1; i <= max; i++) {
                    builder.append(", ").append(i);
                }
                bot.sendMessage(groupId, Message.at(senderId)
                        .append(" 还好，没有踩中炸弹。但是接下来要步步为营了哦。" +
                                "\n当前范围：" + builder)
                        .append(playersInfo));
            } else {
                bot.sendMessage(groupId, Message.at(senderId)
                        .append(" 暂时没有踩中炸弹。缩小范围。" +
                                "\n当前范围：" + min + "~" + max)
                        .append(playersInfo));
            }
        }
    }


    private void commandBombAnswer(MutsumiBot bot, Group group, Member sender) {
        long groupId = group.getId();
        if (!this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(sender.getId())
                    .append(" 当前没有正在进行的数字炸弹游戏。"));
            return;
        }

        GameInfo gameInfo = this.gameInfos.get(groupId);
        bot.sendMessage(groupId, Message.at(sender.getId())
                .append(" 好，有请我扫雷兵 " + mutsumi.getName() + " 公布答案！" +
                        "\n炸弹数字：" + gameInfo.getResult()));
        this.gameInfos.remove(groupId);
    }

    private static MessageChain getPlayersInfo(List<Long> players) {
        if (players.isEmpty()) {
            return MessageChain.EMPTY;
        }

        Iterator<Long> iterator = players.iterator();
        MessageChain.Builder builder = new MessageChain.Builder();
        builder.append(Message.text(">>"))
                .append(Message.at(iterator.next()))
                .append(Message.text("<<"));

        while (iterator.hasNext()) {
            builder.append(Message.text(" ")).append(Message.at(iterator.next()));
        }

        return builder.build();
    }
}
