package nl.dimensiontech.domotics.chargerservice.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("unsecured")
public class UnsecuredConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.warn("### RUNNING APPLICATION UNSECURED ###");

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorize -> authorize
                .anyRequest()
                .permitAll()
        ));

        http.sessionManagement((sessionManagement -> sessionManagement
                .sessionCreationPolicy(STATELESS)
        ));

        return http.build();
    }
}
