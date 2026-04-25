package com.notasfiscais.infrastructure.configuration.jwt;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
@Component
public class JwtService {
    private final JwtProperties jwtProperties;
    private Key secretKey;
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    public String generateToken(WebUser webUser, Pessoa pessoa) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.getExpiration());
        return Jwts.builder()
                .setSubject(pessoa.getNmEmail())
                .claim("cdPessoa", pessoa.getCdPessoa().longValue())
                .claim("cdWebUser", webUser.getCdWebUser().longValue())
                .claim("role", webUser.getTpResponsabilidade())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean validateToken(String token) {
        try { getClaims(token); return true; } catch (Exception e) { return false; }
    }
    public String extractEmail(String token) { return getClaims(token).getSubject(); }
    public Long extractCdPessoa(String token) { return getClaims(token).get("cdPessoa", Long.class); }
    public Long extractCdWebUser(String token) { return getClaims(token).get("cdWebUser", Long.class); }
    public Integer extractRole(String token) { return getClaims(token).get("role", Integer.class); }
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }
}
