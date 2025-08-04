package com.backend.infraestructure.adapters.in.controllers;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.application.IUserService;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final IUserService userService;
    
    // Operaciones CRUD básicas
    
    @PostMapping
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel user) {
        try {
            UserModel createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable String id) {
        try {
            UserModel user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
   
    
    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable String id, @RequestBody UserModel user) {
        try {
            user.setId(id);
            UserModel updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Consultas de servicios por usuario
    
    @GetMapping("/{userId}/services")
    public ResponseEntity<List<ServiceModel>> getServicesForUser(@PathVariable String userId) {
        try {
            List<ServiceModel> services = userService.getServicesForUser(userId);
            return ResponseEntity.ok(services);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
   // @GetMapping("/phone/{phoneNumber}")
   // public ResponseEntity<UserModel> getUserByPhoneNumber(@PathVariable String phoneNumber) {
   //     try {
   //         UserModel user = userService.getUserByPhoneNumber(phoneNumber);
   //         if (user != null) {
   //             return ResponseEntity.ok(user);
   //         } else {
   //             return ResponseEntity.notFound().build();
   //         }
   //     } catch (IllegalArgumentException e) {
   //         return ResponseEntity.badRequest().build();
   //     }
   // }
    
    

    // @GetMapping("/email/{email}")
   // public ResponseEntity<UserModel> getUserByEmail(@PathVariable String email) {
   //     try {
   //         UserModel user = userService.getUserByEmail(email);
   //         if (user != null) {
   //             return ResponseEntity.ok(user);
   //         } else {
   //             return ResponseEntity.notFound().build();
   //         }
   //     } catch (IllegalArgumentException e) {
   //         return ResponseEntity.badRequest().build();
   //     }
   // }
    
   // @GetMapping("/role/{role}")
   // public ResponseEntity<List<UserModel>> getUsersByRole(@PathVariable String role) {
   //     try {
   //         UserModel.Role userRole = UserModel.Role.valueOf(role.toUpperCase());
   //         List<UserModel> users = userService.getUsersByRole(userRole);
   //         return ResponseEntity.ok(users);
   //     } catch (IllegalArgumentException e) {
   //         return ResponseEntity.badRequest().build();
   //     }
   // }
    
   // @GetMapping("/musicians")
   // public ResponseEntity<List<UserModel>> getAvailableMusicians() {
   //     List<UserModel> musicians = userService.getAvailableMusicians();
   //     return ResponseEntity.ok(musicians);
   // }
    
    // @GetMapping("/directors")
    // public ResponseEntity<List<UserModel>> getAvailableDirectors() {
    //     List<UserModel> directors = userService.getAvailableDirectors();
    //     return ResponseEntity.ok(directors);
    // }
    
    
    
    //@GetMapping("/{userId}/services/upcoming")
    //public ResponseEntity<List<ServiceModel>> getUpcomingServicesForUser(@PathVariable String userId) {
    //    try {
    //        List<ServiceModel> services = userService.getUpcomingServicesForUser(userId);
    //        return ResponseEntity.ok(services);
    //    } catch (IllegalArgumentException e) {
    //        return ResponseEntity.badRequest().build();
    //    }
    //} 
   
} 