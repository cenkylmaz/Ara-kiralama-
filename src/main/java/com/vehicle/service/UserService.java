package com.vehicle.service;

import com.vehicle.dao.UserDAO;
import com.vehicle.model.User;
import com.vehicle.exception.AuthException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String email, String password) throws AuthException {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                throw new AuthException("Invalid email or password");
            }

            String hashedPassword = hashPassword(password);
            if (!user.getPassword().equals(hashedPassword)) {
                throw new AuthException("Invalid email or password");
            }

            return user;
        } catch (Exception e) {
            throw new AuthException("Login failed: " + e.getMessage());
        }
    }

    public User register(String name, String email, String password, String userType, LocalDate birthDate)
            throws AuthException {
        try {
            if (userDAO.getUserByEmail(email) != null) {
                throw new AuthException("Email already in use");
            }

            // Validate age for corporate users
            if (userType.equals("CORPORATE")) {
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                if (age < 18) {
                    throw new AuthException("Corporate users must be at least 18 years old");
                }
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(hashPassword(password));
            user.setUserType(UserType.valueOf(userType));
            user.setBirthDate(birthDate);

            if (userDAO.createUser(user)) {
                return user;
            } else {
                throw new AuthException("Registration failed");
            }
        } catch (Exception e) {
            throw new AuthException("Registration failed: " + e.getMessage());
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public int calculateUserAge(User user) {
        return Period.between(user.getBirthDate(), LocalDate.now()).getYears();
    }
}
