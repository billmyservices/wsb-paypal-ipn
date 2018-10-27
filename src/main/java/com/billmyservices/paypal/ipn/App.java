package com.billmyservices.paypal.ipn;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

import static com.billmyservices.paypal.ipn.Log.info;

public class App {
    public static void main(String[] args) throws Exception {
        info("starting wsb-paypal-ipn service");

        final int port = Cfg.integer("port", 9192);
        info("wsb-paypal-ipn server will use %d port", port);

        final Server server = new Server(port);

        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(Digest.class, "/digest");

        final HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{context, new DefaultHandler()});
        server.setHandler(handlers);

        server.start();
        info("wsb-paypal-ipn started");

        server.join();
        info("wsb-paypal-ipn service end");
    }
}
