package nl.dimensiontech.domotics.chargerservice.cucumber;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
public class TestSecurityConfig {

    /* To test if all endpoints are secured with the proper roles we use a basic http authentication
    * instead of (mocking) an extensive keycloak setup. */
    @Bean
    public SecurityFilterChain securityFilterChainUnsecured(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize -> authorize
                .anyRequest()
                .authenticated()
        )).httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails readUser = User
                .withUsername("read-user")
                .password(passwordEncoder().encode("secret"))
                .roles("client_read")
                .build();

        UserDetails writeUser = User
                .withUsername("write-user")
                .password(passwordEncoder().encode("secret"))
                .roles("client_write")
                .build();

        return new InMemoryUserDetailsManager(readUser, writeUser);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
