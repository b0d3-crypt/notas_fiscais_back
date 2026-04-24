package com.notasfiscais.application.usecase.webuser;
import com.notasfiscais.application.dto.auth.LoginRequest;
import com.notasfiscais.application.dto.auth.LoginResponse;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import com.notasfiscais.infrastructure.configuration.jwt.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class LoginUseCase {
    private final IWebUserRepository webUserRepository;
    private final IPessoaRepository pessoaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public LoginUseCase(IWebUserRepository webUserRepository,
                        IPessoaRepository pessoaRepository,
                        BCryptPasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.webUserRepository = webUserRepository;
        this.pessoaRepository = pessoaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public LoginResponse execute(LoginRequest request) throws Exception {
        try {
            WebUser webUser = webUserRepository.getByEmail(request.getEmail());
            if (webUser == null || !passwordEncoder.matches(request.getPassword(), webUser.getPassword())) {
                throw new WebUserException("Credenciais inválidas");
            }
            Pessoa pessoa = pessoaRepository.get(webUser.getCdPessoa());
            String token = jwtService.generateToken(webUser, pessoa);
            return LoginResponse.builder()
                    .token(token)
                    .nmPessoa(pessoa.getNmPessoa())
                    .cdPessoa(pessoa.getCdPessoa().longValue())
                    .role(webUser.getTpResponsabilidade())
                    .build();
        } catch (WebUserException e) {
            throw e;
        } catch (Exception e) {
            throw new WebUserException("Erro ao realizar login: " + e.getMessage());
        }
    }
}
