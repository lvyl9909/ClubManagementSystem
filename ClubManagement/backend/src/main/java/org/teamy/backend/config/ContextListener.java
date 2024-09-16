package org.teamy.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.repository.*;
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
    public static final String VENUE_SERVICE = "venueService";
    public static final String FUNDING_APPLICATION_SERVICE = "fundingApplicationService";



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
        ClubDataMapper clubDataMapper = ClubDataMapper.getInstance(databaseConnectionManager);
        EventDataMapper eventDataMapper = EventDataMapper.getInstance(databaseConnectionManager);
        FacultyAdministratorMapper facultyAdministratorMapper = FacultyAdministratorMapper.getInstance(databaseConnectionManager);
        FundingApplicationMapper fundingApplicationMapper = FundingApplicationMapper.getInstance(databaseConnectionManager);
        RSVPDataMapper rsvpDataMapper = RSVPDataMapper.getInstance(databaseConnectionManager);
        StudentClubDataMapper studentClubDataMapper = StudentClubDataMapper.getInstance(databaseConnectionManager);
        StudentDataMapper studentDataMapper = StudentDataMapper.getInstance(databaseConnectionManager);
        TicketDataMapper ticketDataMapper = TicketDataMapper.getInstance(databaseConnectionManager);
        VenueDataMapper venueDataMapper = VenueDataMapper.getInstance(databaseConnectionManager);

        EventRepository eventRepository = EventRepository.getInstance(eventDataMapper,venueDataMapper,clubDataMapper);
        FundingApplicationRepository fundingApplicationRepository = FundingApplicationRepository.getInstance(fundingApplicationMapper);
        RSVPRepository rsvpRepository = RSVPRepository.getInstance(rsvpDataMapper);
        StudentClubRepository studentClubRepository = StudentClubRepository.getInstance(studentClubDataMapper);
        StudentRepository studentRepository = StudentRepository.getInstance(clubDataMapper,rsvpDataMapper,ticketDataMapper,studentDataMapper,studentClubDataMapper);
        ClubRepository clubRepository = ClubRepository.getInstance(clubDataMapper,eventDataMapper,fundingApplicationMapper,studentRepository,studentClubDataMapper);
        TicketRepository ticketRepository = TicketRepository.getInstance(ticketDataMapper);
        VenueRepository venueRepository = VenueRepository.getInstance(venueDataMapper);

        ClubService clubService = ClubService.getInstance(clubRepository,studentRepository);
        EventService eventService = EventService.getInstance(eventRepository,rsvpRepository,ticketRepository,venueRepository,clubRepository,databaseConnectionManager);
        FundingApplicationService fundingApplicationService =FundingApplicationService.getInstance(fundingApplicationRepository,clubRepository);
        RSVPService rsvpService = RSVPService.getInstance(rsvpRepository);
        StudentClubService studentClubService = StudentClubService.getInstance(studentClubRepository,clubRepository,studentRepository);
        StudentService studentService =StudentService.getInstance(studentRepository);
        VenueService venueService = VenueService.getInstance(venueRepository);
        TicketService ticketService = TicketService.getInstance(ticketRepository,studentRepository,eventDataMapper);
        CustomUserDetailsService customUserDetailsService = CustomUserDetailsService.getInstance(studentRepository,studentClubRepository);

        sce.getServletContext().setAttribute(DATABASE_SERVICE, databaseConnectionManager );
        sce.getServletContext().setAttribute(CLUB_SERVICE, clubService);
        sce.getServletContext().setAttribute(USER_DETAILS_SERVICE, customUserDetailsService);
        sce.getServletContext().setAttribute(EVENT_SERVICE, eventService);
        sce.getServletContext().setAttribute(RSVP_SERVICE, rsvpService);
        sce.getServletContext().setAttribute(TICKET_SERVICE, ticketService);
        sce.getServletContext().setAttribute(STUDENT_SERVICE, studentService);
        sce.getServletContext().setAttribute(STUDENT_CLUB_SERVICE, studentClubService);
        sce.getServletContext().setAttribute(VENUE_SERVICE, venueService);
        sce.getServletContext().setAttribute(FUNDING_APPLICATION_SERVICE, fundingApplicationService);
//        sce.getServletContext().setAttribute(VENUE_SERVICE, VenueService.getInstance(databaseConnectionManager));

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
