package io.github.rikkakawaii0612.mutsumi;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.MutsumiProvider;
import io.github.rikkakawaii0612.mutsumi.impl.MutsumiImpl;
import io.github.rikkakawaii0612.mutsumi.util.BotLogger;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mrxiaom.overflow.BotBuilder;

import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger("Console");

    static void main() {
        // 初始化 Mutsumi 单例
        MutsumiImpl mutsumi = new MutsumiImpl();
        MutsumiProvider.set(mutsumi);

        try {
            mutsumi.getServiceLoader().load();
        } catch (Throwable throwable) {
            LOGGER.error(throwable.getMessage(), throwable);
        }

        mutsumi.runBots();

        Thread consoleThread = new Thread(Main::onConsoleThread);
        consoleThread.setDaemon(true);
        consoleThread.start();

        //TODO: 多账号登录
        JsonNode jsonNode = mutsumi.getConfig().getOrCreate("mutsumi");
        if (jsonNode.has("localBot") && !jsonNode.get("localBot").asBoolean()) {
            BotBuilder.reversed(8080)
                    .token("")
                    .withBotConfiguration(() -> new BotConfiguration() {{
                        this.setBotLoggerSupplier(_ -> new BotLogger());
                    }})
                    .connect();
        }

        // Everlasting Eternity >w<
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void onConsoleThread() {
        Mutsumi mutsumi = MutsumiProvider.getInstance();
        if (!(mutsumi instanceof MutsumiImpl mutsumiImpl)) {
            throw new IllegalStateException(
                    "Mutsumi is not an instance of MutsumiImpl, which means it was modified somewhere!");
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            try {
                String command = scanner.nextLine();
                if (!command.isEmpty()) {
                    if ("reload".equalsIgnoreCase(command)) {
                        mutsumiImpl.getServiceLoader().unload();
                        mutsumiImpl.getServiceLoader().load();
                        continue;
                    }

                    if ("reloadCfg".equalsIgnoreCase(command)) {
                        mutsumiImpl.loadConfigs();
                        continue;
                    }

                    if ("unload".equalsIgnoreCase(command)) {
                        mutsumiImpl.getServiceLoader().unload();
                        continue;
                    }

                    if (command.startsWith("send ")) {
                        mutsumiImpl.getBotBus().sendToLocalBot(command.substring(5));
                        continue;
                    }

                    LOGGER.info("Unknown Command. Use /help for help.");
                }
            } catch (Exception e) {
                LOGGER.warn("Caught exception while processing console command: ", e);
            } catch (Throwable throwable) {
                LOGGER.error("An error occurred while processing console command: ", throwable);
            }
        }
    }
}
