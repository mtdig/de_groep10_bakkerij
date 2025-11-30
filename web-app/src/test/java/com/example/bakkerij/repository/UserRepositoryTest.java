package com.example.bakkerij.repository;

import com.example.bakkerij.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void shouldLoginUser() {
        String sessionId = "session123";
        String username = "john";

        userRepository.login(sessionId, username);

        assertThat(userRepository.isLoggedIn(sessionId)).isTrue();
        assertThat(userRepository.getUsername(sessionId)).isEqualTo(Optional.of(username));
    }

    @Test
    void shouldLogoutUser() {
        String sessionId = "session123";
        userRepository.login(sessionId, "john");

        userRepository.logout(sessionId);

        assertThat(userRepository.isLoggedIn(sessionId)).isFalse();
        assertThat(userRepository.getUsername(sessionId)).isEmpty();
    }

    @Test
    void shouldReturnFalseForNotLoggedInSession() {
        assertThat(userRepository.isLoggedIn("nonexistent")).isFalse();
    }

    @Test
    void shouldGetUsername() {
        String sessionId = "session123";
        userRepository.login(sessionId, "john");

        Optional<String> username = userRepository.getUsername(sessionId);

        assertThat(username).isPresent();
        assertThat(username.get()).isEqualTo("john");
    }

    @Test
    void shouldReturnEmptyForNonLoggedInSession() {
        Optional<String> username = userRepository.getUsername("nonexistent");

        assertThat(username).isEmpty();
    }

    @Test
    void shouldSaveAndGetAddress() {
        String username = "john";
        Address address = new Address("Main St 123", "1234AB", "Amsterdam", "NL");

        userRepository.saveAddress(username, address);

        Optional<Address> result = userRepository.getAddress(username);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(address);
    }

    @Test
    void shouldReturnEmptyForUserWithoutAddress() {
        Optional<Address> result = userRepository.getAddress("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveAndGetPickupDetails() {
        String sessionId = "session123";
        String date = "01/12/2025";
        String time = "10:00";

        userRepository.savePickupDetails(sessionId, date, time);

        Map<String, String> details = userRepository.getPickupDetails(sessionId);
        assertThat(details).containsEntry("date", date);
        assertThat(details).containsEntry("time", time);
    }

    @Test
    void shouldReturnNullForSessionWithoutPickupDetails() {
        Map<String, String> details = userRepository.getPickupDetails("nonexistent");

        assertThat(details).isNull();
    }

    @Test
    void shouldSaveAndGetLastPaymentMethod() {
        String sessionId = "session123";

        userRepository.saveLastPaymentMethod(sessionId, "card");

        String method = userRepository.getLastPaymentMethod(sessionId);
        assertThat(method).isEqualTo("card");
    }

    @Test
    void shouldReturnEmptyStringForSessionWithoutPaymentMethod() {
        String method = userRepository.getLastPaymentMethod("nonexistent");

        assertThat(method).isEmpty();
    }

    @Test
    void shouldOverwritePickupDetails() {
        String sessionId = "session123";
        userRepository.savePickupDetails(sessionId, "01/12/2025", "10:00");

        userRepository.savePickupDetails(sessionId, "02/12/2025", "14:00");

        Map<String, String> details = userRepository.getPickupDetails(sessionId);
        assertThat(details).containsEntry("date", "02/12/2025");
        assertThat(details).containsEntry("time", "14:00");
    }

    @Test
    void shouldOverwritePaymentMethod() {
        String sessionId = "session123";
        userRepository.saveLastPaymentMethod(sessionId, "card");

        userRepository.saveLastPaymentMethod(sessionId, "cash");

        String method = userRepository.getLastPaymentMethod(sessionId);
        assertThat(method).isEqualTo("cash");
    }
}
