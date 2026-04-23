package io.github.rikkakawaii0612.mutsumi;

import io.github.rikkakawaii0612.mutsumi.impl.LocalMutsumiBotImpl;
import io.github.rikkakawaii0612.mutsumi.service.ModuleManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger("Console");
    private static ModuleManager moduleManager;

    static void main() {
        moduleManager = new ModuleManager();

        try {
            moduleManager.loadModules();
        } catch (Throwable throwable) {
            LOGGER.error(throwable.getMessage(), throwable);
        }

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
                    if (command.startsWith("unload ")) {
                        String id = command.substring(7);
                        PluginWrapper wrapper = moduleManager.getPlugin(id);
                        if (wrapper != null && wrapper.getPluginState() == PluginState.STARTED) {
                            moduleManager.stopPlugin(id);
                            moduleManager.unloadPlugin(id);
                            LOGGER.info("Unloaded Service Module '{}'.", id);
                        } else {
                            LOGGER.info("Cannot find Service Module '{}'.", id);
                        }
                        continue;
                    }

                    if (command.startsWith("load ")) {
                        String path = command.substring(5);
                        try {
                            String id = moduleManager.loadPlugin(Paths.get("modules", path));
                            moduleManager.startPlugin(id);
                            LOGGER.info("Loaded Service Module '{}'.", id);
                        } catch (Exception e) {
                            LOGGER.info("Failed to load Service Module '{}': ", path, e);
                        }
                        continue;
                    }

                    if ("reloadAll".equals(command)) {
                        moduleManager.stopPlugins();
                        moduleManager.unloadPlugins();
                        moduleManager.loadModules();
                        continue;
                    }

                    if ("unloadAll".equals(command)) {
                        moduleManager.stopPlugins();
                        moduleManager.unloadPlugins();
                        continue;
                    }

                    if (command.startsWith("send ")) {
                        if (moduleManager.getBot() instanceof LocalMutsumiBotImpl bot) {
                            bot.receiveMessage(command.substring(5));
                        } else {
                            LOGGER.info("No Local Mutsumi Bot present.");
                        }
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
