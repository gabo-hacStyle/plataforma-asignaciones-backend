package com.backend.infraestructure.security;

import java.util.ArrayList;
import java.util.Collection;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.domain.model.UserModel;
import com.backend.domain.port.UserUseCases;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserUseCases userUseCases;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userUseCases.getUserByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }
        
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Crear authorities para cada rol del usuario
        if (user.getRoles() != null) {
            for (UserModel.Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
            }
        }
        
        return new User(user.getEmail(), "", authorities);
    }
} 