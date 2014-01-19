package com.ilirium.bootstrapkendouijaxrs.initialize;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import org.slf4j.LoggerFactory;


@WebServlet(name = "InitializationServlet", urlPatterns = "/InitializationServlet", loadOnStartup = 1)
public class InitializationServlet  extends HttpServlet
{
    private static final long START_TIME = System.currentTimeMillis();
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InitializationServlet.class);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        log.info("Initializing logger ...");
        initializeLogger();
    }

    @Override
    public void destroy()
    {
        log.info("Destroy: total up time = {}", (System.currentTimeMillis() - START_TIME));
    }
    
    /**
     * Initialize Logback(SLF4J)
     */
    private void initializeLogger()
    {
        // ROOT LOGGER
        Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext loggerContext = rootLogger.getLoggerContext();
        loggerContext.reset();
        
        // PATERN
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d [%thread] %highlight(%-5level) %cyan(%logger{0}) - %msg%n");
        encoder.start();

        // CONSOLE APPENDER
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder); 
        appender.start();
        rootLogger.addAppender(appender);
        
        // SET LOG LEVEL
        rootLogger.setLevel(Level.INFO);

        // HIBERNATE LOGGER
        Logger hiberLogger = (Logger)LoggerFactory.getLogger("org.hibernate");
        hiberLogger.setLevel(Level.INFO);

        // WELD LOGGER
        Logger weldLogger = (Logger)LoggerFactory.getLogger("org.jboss.weld");
        weldLogger.setLevel(Level.INFO);
    }
}
