package com.alexvait.accountingapi.security.authorization;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp,
                                    FilterChain chain) throws IOException, ServletException {

        String authHeader = req.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(req, authHeader);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(req, resp);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req, String authHeader) {

        authHeader = authHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
        String user = Jwts.parser()
                .setSigningKey(SecurityConstants.getTokenSecret())
                .parseClaimsJws(authHeader)
                .getBody()
                .getSubject();

        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }

        return null;
    }
}
