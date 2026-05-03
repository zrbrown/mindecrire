package net.eightlives.mindecrire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authc -> authc
                        .requestMatchers("/blog/add").authenticated()
                        .requestMatchers("/blog/edit/*").authenticated()
                        .requestMatchers("/blog/update/*").authenticated()
                        .requestMatchers("/content/image/*").authenticated()
                        .requestMatchers("/actuator/*").authenticated()
                        .requestMatchers("/config/*").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/*"));

        return http.build();
    }
}
