package es.cesar.app.config;

import es.cesar.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The Security configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserService userService;

    /**
     * Instantiates a new Security configuration.
     *
     * @param userService the user service
     */
    @Autowired
    public SecurityConfiguration(@Lazy UserService userService) {
        this.userService = userService;
    }

    /**
     * Granted authority defaults granted authority defaults.
     *
     * @return the granted authority defaults
     */
    @Bean
    public static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("ROLE_");
    }

    /**
     * Security filter chain for Spring Security.
     *
     * @param httpSecurity the http security
     * @param users        the users DetailsService
     *
     * @return the security filter chain
     *
     * @throws Exception the exception that is thrown if an error occurred when building the Object
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, UserDetailsService users) throws Exception {
        // @formatter:off
        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/css/**", "/js/**", "/images/**","/pdfs/**","/videos/**","/stream/**","/csvs/**").permitAll();
            authorize.requestMatchers("/error/**", "/logout", "/", "/home", "/signup", "/login","/about","/params").permitAll();
            authorize.requestMatchers("/admin/**").hasRole("ADMIN");
            authorize.requestMatchers("/user/**").hasRole("USER");
            authorize.anyRequest().authenticated();
        }).formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/", true).permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .rememberMe(rememberMe -> rememberMe.userDetailsService(users));
        // @formatter:on
        return httpSecurity.build();
    }

    /**
     * The Authentication provider for Spring Security.
     *
     * @return the authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * BCrypt password encoder.
     *
     * @return the BCrypt password encoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}