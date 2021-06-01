package com.coursework.demo.security;


import com.coursework.demo.dto.LoginRequest;
import com.coursework.demo.dto.LoginResponse;
import com.coursework.demo.dto.SignUpRequest;
import com.coursework.demo.security.model.Role;
import com.coursework.demo.security.model.User;
import com.coursework.demo.security.repository.RoleRepository;
import com.coursework.demo.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public LoginResponse authenticateRequest(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        List<String> roles = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .jwt(jwt)
                .username(details.getUsername())
                .roles(roles)
                .build();
    }

    public String signUpUser(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username " + request.getUsername() + " already exist");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .roles(mapRoles(request)).build();

        userRepository.save(user);

        return "User was successfully created";
    }


    private Set<Role> mapRoles(SignUpRequest request) {
        return request.getRoles()
                .stream()
                .map(this::mapStringToRole)
                .collect(Collectors.toSet());
    }

    private Role mapStringToRole(String roleString) {
        return roleRepository.findByName(Role.ERole.valueOf(roleString))
                .orElseThrow(() -> new IllegalArgumentException("Wrong name of role" + roleString));
    }
}
