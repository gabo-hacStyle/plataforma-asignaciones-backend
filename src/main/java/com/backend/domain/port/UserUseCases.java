package com.backend.domain.port;

import java.util.List;

import com.backend.domain.model.UserModel;

public interface UserUseCases {
    UserModel createUser(UserModel user);
    UserModel getUserByEmail(String email);
    UserModel getUserById(String id);
    UserModel updateUser(UserModel user);
    void deleteUser(String id);
    List<UserModel> getAllUsers();
    UserModel getUserByPhoneNumber(String phoneNumber);
}
