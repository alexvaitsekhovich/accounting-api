package com.alexvait.accountingapi.security.authentication;

import com.alexvait.accountingapi.security.config.SecurityConfiguration;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.security.model.UserPrincipal;
import com.alexvait.accountingapi.security.springcontext.SpringApplicationContextProvider;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserLoginRequestModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {

        try {
            UserLoginRequestModel userLogin =
                    new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getEmail(),
                            userLogin.getPassword(),
                            new ArrayList<>()
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp,
                                            FilterChain chain, Authentication authResult) {

        String userEmail = ((UserPrincipal) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(userEmail)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_MS_15_MINUTES))
                .signWith(SignatureAlgorithm.HS512, SecurityConfiguration.getTokenSecret())
                .compact();

        UserService userService = SpringApplicationContextProvider.getBean(UserService.class);
        UserDto userDto = userService.getUser(userEmail);

        resp.addHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        resp.addHeader("UserPublicId", userDto.getPublicId());
    }
}
