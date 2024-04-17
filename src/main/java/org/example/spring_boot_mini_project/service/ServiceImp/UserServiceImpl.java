package org.example.spring_boot_mini_project.service.ServiceImp;

import jakarta.mail.MessagingException;
import org.example.spring_boot_mini_project.exception.EmailSendingException;
import org.example.spring_boot_mini_project.model.CustomUserDetail;
import org.example.spring_boot_mini_project.model.User;
import org.example.spring_boot_mini_project.model.dto.request.AppUserRequest;
import org.example.spring_boot_mini_project.repository.UserRepository;
import org.example.spring_boot_mini_project.service.OtpService;
import org.example.spring_boot_mini_project.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private  final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;
    private final OtpService otpService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder encoder, EmailService emailService, OtpService otpService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.emailService = emailService;
        this.otpService = otpService;
    }


    @Override
    public User createUser(AppUserRequest appUserRequest) throws EmailSendingException {

        // Check for existing email (optional)
//        if (userRepository.existsByEmail(appUserRequest.getEmail())) {
//            throw new IllegalArgumentException("Email address already exists");
//        }
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(appUserRequest.getEmail()));
        if (existingUser.isPresent()) {
            throw new EmailSendingException("Email address already exists");
        }
        String otpCode = otpService.generateOtp();

        try {
            emailService.sendOtpEmail(appUserRequest.getEmail(), "OTP", otpCode);
        }  catch (Exception e) {
            // Handle unexpected exceptions (log or re-throw)
            throw new RuntimeException("Unexpected error during user creation", e);
        }

        String encodedPassword = encoder.encode(appUserRequest.getPassword());
        appUserRequest.setPassword(encodedPassword);

        return userRepository.insert(appUserRequest);
    }

    @Override
    public List<User> getAllUser() {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

//        if(user == null){
//            throw new NotFoundException("could not found user..!!");
//        }
        return new CustomUserDetail(user);

    }
}
