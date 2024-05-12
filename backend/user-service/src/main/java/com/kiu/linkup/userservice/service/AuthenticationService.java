package com.kiu.linkup.userservice.service;


import com.kiu.linkup.userservice.dto.RegistrationRequest;
import com.kiu.linkup.userservice.entity.Token;
import com.kiu.linkup.userservice.entity.User;
import com.kiu.linkup.userservice.enums.EmailTemplateName;
import com.kiu.linkup.userservice.repository.RoleRepository;
import com.kiu.linkup.userservice.repository.TokenRepository;
import com.kiu.linkup.userservice.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Value("${application.email.activation-code-length}")
    private int activationCodeLength;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role USER was not initialized"));
        var user = User.builder().firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .major(request.getMajor())
                .minor(request.getMinor())
                .year(request.getYear())
                .accountLocked(false)
                .isEnabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Kiu-linkup Account Activation");
    }

    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken = generateActivationCode(activationCodeLength);
        var token = Token.builder()
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            codeBuilder.append(random.nextInt(11) - 1);
        }
        return codeBuilder.toString();
    }

}
