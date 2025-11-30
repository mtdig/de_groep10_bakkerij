package com.example.bakkerij.service;

import com.example.bakkerij.model.Address;
import com.example.bakkerij.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String sessionId, String firstname, String password) {
        // Simple validation: firstname must be a word (letters only), password can be anything
        if (firstname != null && !firstname.trim().isEmpty() && firstname.matches("[a-zA-Z]+")) {
            userRepository.login(sessionId, firstname);
            return true;
        }
        return false;
    }

    public void logout(String sessionId) {
        userRepository.logout(sessionId);
    }

    public Optional<String> getUsername(String sessionId) {
        return userRepository.getUsername(sessionId);
    }

    public boolean isLoggedIn(String sessionId) {
        return userRepository.isLoggedIn(sessionId);
    }

    public void updateAddress(String username, Address address) {
        userRepository.saveAddress(username, address);
    }

    public Optional<Address> getAddress(String username) {
        return userRepository.getAddress(username);
    }

    public void savePickupDetails(String sessionId, String date, String time) {
        userRepository.savePickupDetails(sessionId, date, time);
    }

    public Map<String, String> getPickupDetails(String sessionId) {
        return userRepository.getPickupDetails(sessionId);
    }

    public void saveLastPaymentMethod(String sessionId, String method) {
        userRepository.saveLastPaymentMethod(sessionId, method);
    }

    public String getLastPaymentMethod(String sessionId) {
        return userRepository.getLastPaymentMethod(sessionId);
    }
}
