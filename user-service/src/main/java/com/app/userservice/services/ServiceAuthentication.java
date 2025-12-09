package com.app.userservice.services;

import com.app.userservice.entities.AppRole;
import com.app.userservice.entities.AppUser;
import com.app.userservice.repositories.AppRoleRepository;
import com.app.userservice.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ServiceAuthentication implements IServiceAuthentication {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AppUser createAppUser(AppUser appUser) {
        AppUser existingUser = appUserRepository.findByUsername(appUser.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Username déjà utilisé !");
        }

        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole createAppRole(AppRole appRole) {
        AppRole existingRole = appRoleRepository.findByRole(appRole.getRole());
        if (existingRole != null) {
            throw new RuntimeException("Ce rôle existe déjà !");
        }
        appRoleRepository.save(appRole);
        return null;
    }

    @Override
    public void addRoleToUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRole(role);

        if (appUser == null) {
            throw new RuntimeException("Utilisateur introuvable !");
        }
        if (appRole == null) {
            throw new RuntimeException("Rôle introuvable !");
        }

        appUser.getRoles().add(appRole);
        appUserRepository.save(appUser);

    }

}
