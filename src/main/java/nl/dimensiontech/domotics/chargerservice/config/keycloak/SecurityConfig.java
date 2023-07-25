package nl.dimensiontech.domotics.chargerservice.config.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter();
    }

    @Bean
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/actuator/*")
                .permitAll()
                .anyRequest()
                .authenticated()
        ));

        http.oauth2ResourceServer((resourceServer -> resourceServer
                .jwt(jwtConfigurer -> jwtConfigurer
                        .jwtAuthenticationConverter(jwtAuthConverter()))
        ));

        http.sessionManagement((sessionManagement -> sessionManagement
                .sessionCreationPolicy(STATELESS)
        ));

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
    public SecurityFilterChain securityFilterChainUnsecured(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize -> authorize
                .anyRequest()
                .permitAll()
        ));

        return http.build();
    }
}
