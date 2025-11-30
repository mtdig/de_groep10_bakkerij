package com.example.bakkerij.service;

import com.example.bakkerij.model.Address;
import com.example.bakkerij.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void shouldLoginWithValidCredentials() {
        String sessionId = "session123";
        String firstname = "John";
        String password = "password";

        boolean result = userService.login(sessionId, firstname, password);

        assertThat(result).isTrue();
        verify(userRepository).login(sessionId, firstname);
    }

    @Test
    void shouldNotLoginWithInvalidFirstname() {
        String sessionId = "session123";

        assertThat(userService.login(sessionId, "John123", "password")).isFalse();
        assertThat(userService.login(sessionId, "", "password")).isFalse();
        assertThat(userService.login(sessionId, null, "password")).isFalse();
        assertThat(userService.login(sessionId, "   ", "password")).isFalse();

        verify(userRepository, never()).login(anyString(), anyString());
    }

    @Test
    void shouldLogout() {
        String sessionId = "session123";

        userService.logout(sessionId);

        verify(userRepository).logout(sessionId);
    }

    @Test
    void shouldGetUsername() {
        String sessionId = "session123";
        when(userRepository.getUsername(sessionId)).thenReturn(Optional.of("John"));

        Optional<String> username = userService.getUsername(sessionId);

        assertThat(username).isPresent();
        assertThat(username.get()).isEqualTo("John");
    }

    @Test
    void shouldCheckIfLoggedIn() {
        String sessionId = "session123";
        when(userRepository.isLoggedIn(sessionId)).thenReturn(true);

        boolean loggedIn = userService.isLoggedIn(sessionId);

        assertThat(loggedIn).isTrue();
    }

    @Test
    void shouldUpdateAddress() {
        String username = "John";
        Address address = new Address("Main St 123", "1234AB", "Amsterdam", "NL");

        userService.updateAddress(username, address);

        verify(userRepository).saveAddress(username, address);
    }

    @Test
    void shouldGetAddress() {
        String username = "John";
        Address address = new Address("Main St 123", "1234AB", "Amsterdam", "NL");
        when(userRepository.getAddress(username)).thenReturn(Optional.of(address));

        Optional<Address> result = userService.getAddress(username);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(address);
    }

    @Test
    void shouldSavePickupDetails() {
        String sessionId = "session123";
        String date = "01/12/2025";
        String time = "10:00";

        userService.savePickupDetails(sessionId, date, time);

        verify(userRepository).savePickupDetails(sessionId, date, time);
    }

    @Test
    void shouldGetPickupDetails() {
        String sessionId = "session123";
        Map<String, String> details = Map.of("date", "01/12/2025", "time", "10:00");
        when(userRepository.getPickupDetails(sessionId)).thenReturn(details);

        Map<String, String> result = userService.getPickupDetails(sessionId);

        assertThat(result).containsEntry("date", "01/12/2025");
        assertThat(result).containsEntry("time", "10:00");
    }

    @Test
    void shouldSaveLastPaymentMethod() {
        String sessionId = "session123";

        userService.saveLastPaymentMethod(sessionId, "card");

        verify(userRepository).saveLastPaymentMethod(sessionId, "card");
    }

    @Test
    void shouldGetLastPaymentMethod() {
        String sessionId = "session123";
        when(userRepository.getLastPaymentMethod(sessionId)).thenReturn("card");

        String method = userService.getLastPaymentMethod(sessionId);

        assertThat(method).isEqualTo("card");
    }
}
