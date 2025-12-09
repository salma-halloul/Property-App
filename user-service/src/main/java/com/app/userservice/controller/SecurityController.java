package com.app.userservice.controller;
import com.app.userservice.entities.AppUser;
import com.app.userservice.model.AuthRequest;
import com.app.userservice.repositories.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final AppUserRepository appUserRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Récupérer l'utilisateur depuis la base de données pour obtenir son ID
            AppUser user = appUserRepository.findByUsername(username);
            if (user == null) {
                return Map.of("error", "User not found");
            }

            String scope = authentication.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.joining(" "));

            Instant instant = Instant.now();
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                    .subject(user.getId().toString())  // Mettre l'ID au lieu du username
                    .claim("scope", scope)
                    .build();

            JwtEncoderParameters params = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            String jwt = jwtEncoder.encode(params).getTokenValue();
            return Map.of("access-token", jwt);

        } catch (AuthenticationException e) {
            return Map.of("error", "Invalid username or password");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
        }

        // Récupérer l'ID depuis le token (subject contient maintenant l'ID)
        String userId = authentication.getName();
        AppUser user = appUserRepository.findById(Long.parseLong(userId)).orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found"));
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("roles", authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return ResponseEntity.ok(profile);
    }
}
