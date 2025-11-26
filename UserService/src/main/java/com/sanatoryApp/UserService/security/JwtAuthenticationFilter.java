package com.sanatoryApp.UserService.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        boolean shouldSkip = path.startsWith("/auth/") ||
                path.equals("/error") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");

        log.debug("Path: {}, Skip JWT filter: {}", path, shouldSkip);
        return shouldSkip;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

       // Get header authorization from request
       String authHeader= request.getHeader("Authorization");


        // Request without token - skip JWT validation
       if(authHeader==null || !authHeader.startsWith("Bearer ")){
           log.debug("No JWT token found in request to {}", request.getRequestURI());
           filterChain.doFilter(request,response); // Does not configure SecurityContext, goes to controller
           return;
       }


        //Request with token - validate and configure SecurityContext
       String token= authHeader.substring(7);
       try {
           if(!jwtUtils.validateToken(token)){
               log.warn("Invalid JWT token in request to {}", request.getRequestURI());
               filterChain.doFilter(request, response);
               return;
           }

           //Extract info from token
           String username= jwtUtils.getUsernameFromToken(token);
           List<String>roles=jwtUtils.getRolesFromToken(token);
           log.debug("JWT validated for user: {} with roles: {}", username, roles);

           //Convert roles to GrantedAuthority
           List<GrantedAuthority> authorities = roles.stream()
                   .map(SimpleGrantedAuthority::new)
                   .collect(Collectors.toList());

           //Create Authentication object
           UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(username,null,authorities);

           //Extra information for auditing/security purposes (optional)
           authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


           //Set SecurityContext
           SecurityContextHolder.getContext().setAuthentication(authentication);
           log.debug("SecurityContext configured for user: {}", username);

       }catch (Exception e) {
           log.error("Cannot set user authentication: {}", e.getMessage());
       }

        filterChain.doFilter(request, response);

    }
}
