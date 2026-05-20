package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserUseCase {
    User createUser(User user);
    User updateUser(String id, User user);
    Optional<User> getUserById(String id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    List<User> getUsersByRole(String rol);
    void deleteUser(String id);
    User changeAccountStatus(String id, String status);
    User changePassword(String id, String newPassword);
}
