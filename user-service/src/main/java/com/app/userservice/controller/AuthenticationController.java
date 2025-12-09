package com.app.userservice.controller;
import com.app.userservice.entities.AppRole;
import com.app.userservice.entities.AppUser;
import com.app.userservice.services.IServiceAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IServiceAuthentication iServiceAuthentication;

    @PostMapping("/user")
    public AppUser addNewUser(@RequestBody AppUser appUser) {
        return iServiceAuthentication.createAppUser(appUser);
    }

    @PostMapping("/role")
    public AppRole addNewRole(@RequestBody AppRole appRole) {
        return iServiceAuthentication.createAppRole(appRole);
    }

    @PostMapping("/addRoleToUser")
    public void addRoleToUser(@RequestParam String username, @RequestParam String role) {
        iServiceAuthentication.addRoleToUser(username, role);
    }

}
