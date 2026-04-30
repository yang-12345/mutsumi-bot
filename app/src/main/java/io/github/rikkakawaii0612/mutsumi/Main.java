package io.github.rikkakawaii0612.mutsumi;

import io.github.rikkakawaii0612.mutsumi.impl.MutsumiImpl;
import io.github.rikkakawaii0612.mutsumi.util.BotLogger;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mrxiaom.overflow.BotBuilder;

import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger("Console");
    private static MutsumiImpl mutsumi;

    static void main() {
        mutsumi = new MutsumiImpl();

        try {
            mutsumi.getServiceLoader().load();
        } catch (Throwable throwable) {
            LOGGER.error(throwable.getMessage(), throwable);
        }

        mutsumi.runBots();

        //TODO: 多账号登录
//        BotBuilder.reversed(8080)
//                .token("")
//                .withBotConfiguration(() -> new BotConfiguration() {{
//                    this.setBotLoggerSupplier(_ -> new BotLogger());
//                }})
//                .connect();

        // Everlasting Eternity >w<
        Thread consoleThread = new Thread(Main::onConsoleThread);
        consoleThread.setDaemon(true);
        consoleThread.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void onConsoleThread() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            try {
                String command = scanner.nextLine();
                if (!command.isEmpty()) {
                    if ("reload".equalsIgnoreCase(command)) {
                        mutsumi.getServiceLoader().unload();
                        mutsumi.getServiceLoader().load();
                        continue;
                    }

                    if ("unload".equalsIgnoreCase(command)) {
                        mutsumi.getServiceLoader().unload();
                        continue;
                    }

                    if (command.startsWith("send ")) {
                        mutsumi.getBotBus().sendToLocalBot(command.substring(5));
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
