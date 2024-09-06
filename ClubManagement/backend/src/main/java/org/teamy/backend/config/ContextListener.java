package org.teamy.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.security.CustomUserDetailsService;
import org.teamy.backend.security.repository.JwtTokenServiceImpl;
import org.teamy.backend.security.repository.PostgresRefreshTokenRepository;
import org.teamy.backend.service.*;

@WebListener
public class ContextListener implements ServletContextListener {
    public static final String CLUB_SERVICE = "clubService";
    public static final String STUDENT_SERVICE = "studentService";
    public static final String STUDENT_CLUB_SERVICE = "studentClubService";
    public static final String EVENT_SERVICE = "eventService";
    public static final String RSVP_SERVICE = "rsvpService";
    public static final String TICKET_SERVICE = "ticketService";

    public static final String DATABASE_SERVICE = "databaseService";
    public static final String MAPPER = "mapper";
    public static final String TOKEN_SERVICE = "tokenService";
    public static final String USER_DETAILS_SERVICE = "userDetailsService";
    public static final String PASSWORD_ENCODER = "passwordEncoder";
    public static final String DOMAIN = "domain";
    public static final String COOKIE_TIME_TO_LIVE_SECONDS = "cookieTimeToLiveSeconds";
    public static final String SECURE_COOKIES = "secureCookies";
    private static final String PROPERTY_JDBC_URI = "jdbc.uri";
    private static final String PROPERTY_JDBC_USERNAME = "jdbc.username";
    private static final String PROPERTY_JDBC_PASSWORD = "jdbc.password";
    private static final String PROPERTY_JWT_SECRET = "jwt.secret";
    private static final String PROPERTY_JWT_TIME_TO_LIVE_SECONDS = "jwt.timeToLive.seconds";
    private static final String PROPERTY_JWT_ISSUER = "jwt.issuer";
    private static final String PROPERTY_COOKIES_TIME_TO_LIVE_SECONDS = "cookies.timeToLive.seconds";
    private static final String PROPERTY_COOKIES_DOMAIN = "cookies.domain";
    private static final String PROPERTY_COOKIES_SECURE = "cookies.secure";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var databaseConnectionManager = new DatabaseConnectionManager(
                System.getProperty(PROPERTY_JDBC_URI),
                System.getProperty(PROPERTY_JDBC_USERNAME),
                System.getProperty(PROPERTY_JDBC_PASSWORD));
        databaseConnectionManager.init();

        sce.getServletContext().setAttribute(DATABASE_SERVICE, databaseConnectionManager );
        sce.getServletContext().setAttribute(CLUB_SERVICE, new ClubService(new ClubDataMapper(databaseConnectionManager)));
        sce.getServletContext().setAttribute(USER_DETAILS_SERVICE, new CustomUserDetailsService(new StudentDataMapper(databaseConnectionManager)));
        sce.getServletContext().setAttribute(EVENT_SERVICE, new EventService(new EventDataMapper(databaseConnectionManager)));
        sce.getServletContext().setAttribute(RSVP_SERVICE, new RSVPService(new RSVPDataMapper(databaseConnectionManager)));
        sce.getServletContext().setAttribute(TICKET_SERVICE, new TicketService(new TicketDataMapper(databaseConnectionManager)));
        sce.getServletContext().setAttribute(STUDENT_SERVICE, new StudentService(new StudentDataMapper(databaseConnectionManager),new ClubService(new ClubDataMapper(databaseConnectionManager)),new RSVPService(new RSVPDataMapper(databaseConnectionManager)),new TicketService(new TicketDataMapper(databaseConnectionManager))));
        sce.getServletContext().setAttribute(STUDENT_CLUB_SERVICE, new StudentClubService(new StudentClubDataMapper(databaseConnectionManager)));

        var mapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .failOnUnknownProperties(false)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .build();

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        sce.getServletContext().setAttribute(MAPPER, mapper);
        sce.getServletContext().setAttribute(TOKEN_SERVICE, new JwtTokenServiceImpl(
                System.getProperty(PROPERTY_JWT_SECRET),
                Integer.parseInt(System.getProperty(PROPERTY_JWT_TIME_TO_LIVE_SECONDS)),
                System.getProperty(PROPERTY_JWT_ISSUER),
                new PostgresRefreshTokenRepository(databaseConnectionManager)
        ));

        sce.getServletContext().setAttribute(DOMAIN, System.getProperty(PROPERTY_COOKIES_DOMAIN));
        sce.getServletContext().setAttribute(COOKIE_TIME_TO_LIVE_SECONDS, Integer.parseInt(System.getProperty(PROPERTY_COOKIES_TIME_TO_LIVE_SECONDS)));
        sce.getServletContext().setAttribute(SECURE_COOKIES, Boolean.parseBoolean(System.getProperty(PROPERTY_COOKIES_SECURE)));

        ServletContextListener.super.contextInitialized(sce);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
