package com.gotravel.gotravel.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotravel.gotravel.entity.Token;
import com.gotravel.gotravel.repository.TokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

// XÁC THỰC VÀ ỦY QUYỀN

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String Secret_key = "123";

	private final TokenRepository tokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader(AUTHORIZATION);

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

			try {
				String token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256(Secret_key.getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodeJWT = verifier.verify(token);
				String username = decodeJWT.getSubject();
				String[] roles = decodeJWT.getClaim("roles").asArray(String.class);

				Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
				Arrays.stream(roles).forEach(role -> {
					authorities.add(new SimpleGrantedAuthority(role));
				});

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				filterChain.doFilter(request, response);

			} catch (TokenExpiredException e) {

				// Lấy token từ chuỗi authorization
				String token = authorizationHeader.substring("Bearer ".length());

				// Cập nhật trạng thái của token
				Optional<Token> expiredToken = tokenRepository.findByToken(token);

				if (expiredToken != null) {
					Token EToken = expiredToken.get();
					EToken.setExpired(true);
					EToken.setRevoked(true);
					tokenRepository.save(EToken);
				}

				response.setHeader("error", e.getMessage());

				// trả về thông báo lỗi khi token hết hạn
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				Map<String, String> error = new HashMap<>();
				error.put("message", "Phiên làm việc đã kết thúc, vui lòng đăng nhập lại.");
				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}

		} else {
			filterChain.doFilter(request, response);
		}

	}

}
