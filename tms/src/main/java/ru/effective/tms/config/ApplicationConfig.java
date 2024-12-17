package ru.effective.tms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@ComponentScan("ru.effective.tms")
@PropertySource("classpath:config/application.yml")
@Configuration
public class ApplicationConfig {
    /**
     * Service for AuthenticationProvider init.
     *
     * @see #authenticationProvider()
     */
    private final UserDetailsService userDetailsService;
    /**
     * Bean {@link DaoAuthenticationProvider}
     * for {@link AuthenticationManager} configure.
     *
     * @return {@link DaoAuthenticationProvider} with updated configuration
     * @see #authenticationManager(AuthenticationConfiguration)
     * @see UserDetailsService
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean {@link PasswordEncoder} for security configure.
     *
     * @return default {@link BCryptPasswordEncoder}
     * @see #authenticationProvider()
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Bean {@link AuthenticationManager} for authentication configure.
     *
     * @param authenticationConfiguration {@link AuthenticationConfiguration} to set authentication.
     * @return {@link AuthenticationManager} with updated configuration.
     * @throws Exception if {@link HttpSecurity} throws exception.
     * @see SecurityConfiguration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
