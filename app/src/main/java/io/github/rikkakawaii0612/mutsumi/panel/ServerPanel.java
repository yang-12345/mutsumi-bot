package io.github.rikkakawaii0612.mutsumi.panel;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger("WebSocket");

    public static void start(int port) throws Exception {
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // 1. 注册 WebSocket 端点
        WebSocketServerContainerInitializer.configure(context, (_, wsContainer) -> {
            wsContainer.addEndpoint(GroupMessageWebSocket.class);
        });

        // 2. 注册 REST API Servlets
//        context.addServlet(new ServletHolder(new SendMessageServlet()), "/api/sendGroupMsg");
//        context.addServlet(new ServletHolder(new PluginListServlet()), "/api/plugins");
//        context.addServlet(new ServletHolder(new ReloadPluginsServlet()), "/api/plugins/reload");

        // 3. 提供静态资源（管理面板 HTML/JS/CSS）
        // 假设你的资源放在 src/main/resources/webapp 目录下
        context.setResourceBase(ServerPanel.class.getClassLoader().getResource("webapp").toExternalForm());
        context.addServlet(DefaultServlet.class, "/");

        server.start();
        LOGGER.info("Management Panel launched: http://localhost:{}", port);
    }
}