package nl.dimensiontech.domotics.chargerservice.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    public static final String ROLE_PREFIX = "ROLE_";

    @Value("${jwt.auth.converter.client-id}")
    private String resourceId;

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        var authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipleClaimName(jwt));
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;

        if (principleAttribute != null) {
            claimName = principleAttribute;
        }

        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (jwt.getClaim("resource_access") == null) {
            return Set.of();
        }

        resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null) {
            return Set.of();
        }

        try {
            resource = (Map<String, Object>) resourceAccess.get(resourceId);
            resourceRoles = (Collection<String>) resource.get("roles");
        } catch (ClassCastException e) {
            log.warn("Could not extract resource roles from JWT.");
            return Set.of();
        }

        return resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }
}
