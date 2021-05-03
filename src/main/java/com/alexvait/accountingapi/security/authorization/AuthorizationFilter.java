package com.alexvait.accountingapi.security.authorization;

import com.alexvait.accountingapi.security.config.JwtTokenContainer;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.security.model.UserPrincipal;
import com.alexvait.accountingapi.security.springcontext.SpringApplicationContextProvider;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenContainer jwtTokenContainer;

    public AuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenContainer jwtTokenContainer) {
        super(authenticationManager);
        this.jwtTokenContainer = jwtTokenContainer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp,
                                    FilterChain chain) throws IOException, ServletException {

        String authHeader = req.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(authHeader);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(req, resp);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authHeader) throws JwtException {

        String updatedAuthHeader = authHeader.replace(SecurityConstants.TOKEN_PREFIX, "");

        try {
            String userEmail = Jwts.parser()
                    .setSigningKey(jwtTokenContainer.getTokenSecret())
                    .parseClaimsJws(updatedAuthHeader)
                    .getBody()
                    .getSubject();

            if (userEmail != null) {
                UserRepository userRepository = SpringApplicationContextProvider.getBean(UserRepository.class);

                UserEntity userEntity = userRepository.findByEmail(userEmail);
                if (userEntity != null) {
                    UserPrincipal userPrincipal = new UserPrincipal(userEntity);
                    return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                }
            }
        } catch (ExpiredJwtException ex) {
            log.error(ex.getMessage());
        }

        return null;
    }
}
