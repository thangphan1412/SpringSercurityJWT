package org.example.security;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// để Spring Boot tự động phát hiện và quản lý Bean này
@Component
//tự tạo constructor cho các field final (nếu có).
@RequiredArgsConstructor
//OncePerRequestFilter: class cha đảm bảo filter này chỉ được gọi một lần duy nhất mỗi request.
public class JwtAuthenticationFIlter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
  //phương thức sẽ được gọi tự động trong chuỗi xử lý request của Spring Security.
    protected void doFilterInternal(
                                    @NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        token = authHeader.substring(7);
        userEmail = jwtService.extractUsername(token);
    }
}
