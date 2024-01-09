package com.api.gestion.jwt;

// Clase que me permite poder validar el token

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter { // extendemos de OncePerRequestFilter para que solo se ejecute una sola vez

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    Claims claims = null;
    private String username = null;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().matches("/user/login|user/forgotPassword|/user/signup")){
            filterChain.doFilter(request, response);
        }else{
            String authorizationHeader = request.getHeader("Authorization");
            String token = null;

            if (authorizationHeader != null &&  authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7);
                username = jwtUtil.extractUserName(token);
                claims = jwtUtil.extractAllClaims(token);
            }

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) { // y si Aun no se ha autenticado el usuario
                UserDetails userDetails = customerDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    new WebAuthenticationDetailsSource().buildDetails(request);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    public Boolean isAdmin(){ // Verificamos si es admin
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }
    public Boolean isUser(){ // Verificamos si es user
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }
    public String getCurrentUser(){
        return username;
    }

}
