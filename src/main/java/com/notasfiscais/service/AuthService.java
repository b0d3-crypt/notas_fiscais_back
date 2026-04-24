package com.notasfiscais.service;

import com.notasfiscais.dto.LoginRequest;
import com.notasfiscais.dto.LoginResponse;
import com.notasfiscais.entity.WebUser;
import com.notasfiscais.repository.WebUserRepository;
import com.notasfiscais.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final WebUserRepository webUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(WebUserRepository webUserRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        WebUser webUser = webUserRepository.findByPessoaNmEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), webUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(webUser);

        return LoginResponse.builder()
                .token(token)
                .nmPessoa(webUser.getPessoa().getNmPessoa())
                .cdPessoa(webUser.getPessoa().getCdPessoa().longValue())
                .role(webUser.getTpResponsabilidade())
                .build();
    }
}
