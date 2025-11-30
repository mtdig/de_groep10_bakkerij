package com.example.bakkerij.repository;

import com.example.bakkerij.model.Address;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {
    private final Map<String, String> sessions = new HashMap<>(); // sessionId -> username
    private final Map<String, Address> addresses = new HashMap<>(); // username -> address
    private final Map<String, Map<String, String>> pickupDetails = new HashMap<>(); // sessionId -> pickup info
    private final Map<String, String> lastPaymentMethod = new HashMap<>(); // sessionId -> payment method

    public void login(String sessionId, String username) {
        sessions.put(sessionId, username);
    }

    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    public Optional<String> getUsername(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public boolean isLoggedIn(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void saveAddress(String username, Address address) {
        addresses.put(username, address);
    }

    public Optional<Address> getAddress(String username) {
        return Optional.ofNullable(addresses.get(username));
    }

    public void savePickupDetails(String sessionId, String date, String time) {
        pickupDetails.computeIfAbsent(sessionId, k -> new HashMap<>());
        pickupDetails.get(sessionId).put("date", date);
        pickupDetails.get(sessionId).put("time", time);
    }

    public Map<String, String> getPickupDetails(String sessionId) {
        return pickupDetails.get(sessionId);
    }

    public void saveLastPaymentMethod(String sessionId, String method) {
        lastPaymentMethod.put(sessionId, method);
    }

    public String getLastPaymentMethod(String sessionId) {
        return lastPaymentMethod.getOrDefault(sessionId, "");
    }
}
