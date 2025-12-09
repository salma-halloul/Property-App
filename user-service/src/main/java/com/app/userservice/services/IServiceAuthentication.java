package com.app.userservice.services;


import com.app.userservice.entities.AppRole;
import com.app.userservice.entities.AppUser;

public interface IServiceAuthentication {
    public AppUser createAppUser(AppUser appUser);
    public AppRole createAppRole(AppRole appRole);
    public void addRoleToUser(String username, String role);
}
