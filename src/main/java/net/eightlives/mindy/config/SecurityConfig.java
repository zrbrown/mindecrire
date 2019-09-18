package net.eightlives.mindy.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/blog/add**").authenticated()
                .antMatchers("/blog/**/edit**").authenticated()
                .antMatchers("/blog/**/update**").authenticated()
                .antMatchers("/actuator/**").authenticated()
                .antMatchers("/config/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403");
        // TODO is there a way to do this without ignoring CSRF?
        http.csrf()
                .ignoringAntMatchers("/content/**")
                .ignoringAntMatchers("/actuator/**");
    }
}
