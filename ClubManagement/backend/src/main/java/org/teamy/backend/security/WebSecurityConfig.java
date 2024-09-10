package org.teamy.backend.security;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.config.ContextListener;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.repository.StudentRepository;
import org.teamy.backend.security.model.Role;
import org.teamy.backend.security.repository.TokenService;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements ServletContextAware {
    private static final String PROPERTY_CORS_ORIGINS_UI = "cors.origins.ui";
    private static final RequestMatcher PROTECTED_URLS = new AntPathRequestMatcher("/");
    private static final RequestMatcher ADMIN_PROTECTED_URLS = new AntPathRequestMatcher("/admin/**");
    private static final RequestMatcher STUDENT_PROTECTED_URLS = new AntPathRequestMatcher("/student/**");

    private static final String PROPERTY_ADMIN_USERNAME = "admin.username";
    private static final String PROPERTY_ADMIN_PASSWORD = "admin.password";

    private TokenService jwtTokenService;
    private ServletContext servletContext;
    private DatabaseConnectionManager databaseConnectionManager;

    //定义表单登陆的方法
    @Bean
    public UserDetailsService userDetailsService() {
        ClubDataMapper clubDataMapper = ClubDataMapper.getInstance(databaseConnectionManager);
        RSVPDataMapper rsvpDataMapper = RSVPDataMapper.getInstance(databaseConnectionManager);
        TicketDataMapper ticketDataMapper = TicketDataMapper.getInstance(databaseConnectionManager);
        StudentDataMapper studentDataMapper = StudentDataMapper.getInstance(databaseConnectionManager);
        StudentClubDataMapper studentClubDataMapper = StudentClubDataMapper.getInstance(databaseConnectionManager);

        StudentRepository userRepository =  StudentRepository.getInstance(clubDataMapper,rsvpDataMapper,ticketDataMapper,studentDataMapper,studentClubDataMapper);
        return new CustomUserDetailsService(userRepository);
    }
    //未认证的入口
    @Bean
    AuthenticationEntryPoint unauthorizedEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    //配置身份验证管理器，分别定义表单登陆和令牌登陆的入口
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService, TokenAuthenticationProvider authenticationProvider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .and()
                .authenticationProvider(authenticationProvider)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, TokenAuthenticationFilter tokenAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        return http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(authenticationEntryPoint, PROTECTED_URLS)
                .and()
                .addFilterBefore(tokenAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/token").permitAll()
                        .requestMatchers(ADMIN_PROTECTED_URLS)
                        .hasRole(Role.ADMIN.name())
                        .requestMatchers(STUDENT_PROTECTED_URLS)
//                        .permitAll()
                        .hasRole(Role.USER.name())
                        .anyRequest()
                        .permitAll())
                .authenticationManager(authenticationManager)
                .cors(Customizer.withDefaults())
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .build();
    }

    //配置身份验证提供者，基于令牌的身份验证
    @Bean
    public TokenAuthenticationProvider tokenAuthenticationProvider(UserDetailsService userDetailsService) {
        return new TokenAuthenticationProvider(userDetailsService);
    }
    //配置CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(System.getProperty(PROPERTY_CORS_ORIGINS_UI)));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","DELETE"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //认证成功之后去哪
    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy((request, response, url) -> {
            // 根据条件设置重定向URL
//            String redirectUrl = determineTargetUrl(request, response);
//            response.sendRedirect(redirectUrl);
        });
        return successHandler;
    }
    private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        // 例如，基于用户角色进行重定向
        if (request.isUserInRole("ADMIN")) {
            return "/admin/home";
        } else {
            return "/student/home";
        }
    }
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(AuthenticationManager authenticationManager, SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler) {
        var filter = new TokenAuthenticationFilter(STUDENT_PROTECTED_URLS, jwtTokenService);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(simpleUrlAuthenticationSuccessHandler);
        return filter;
    }
    private UserDetails adminUser(PasswordEncoder passwordEncoder) {
        return new User(System.getProperty(PROPERTY_ADMIN_USERNAME),
                passwordEncoder.encode(System.getProperty(PROPERTY_ADMIN_PASSWORD)),
                Collections.singleton(Role.ADMIN.toAuthority()));
    }
    private UserDetails nonAdminUser(PasswordEncoder passwordEncoder) {
        return new User("user",
                passwordEncoder.encode("user"),
                Collections.singleton(Role.USER.toAuthority()));
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        var passwordEncoder = new BCryptPasswordEncoder();
        servletContext.setAttribute(ContextListener.PASSWORD_ENCODER, passwordEncoder);
        return passwordEncoder;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        databaseConnectionManager = (DatabaseConnectionManager) servletContext.getAttribute(ContextListener.DATABASE_SERVICE);
        jwtTokenService = (TokenService) servletContext.getAttribute(ContextListener.TOKEN_SERVICE);
    }
}