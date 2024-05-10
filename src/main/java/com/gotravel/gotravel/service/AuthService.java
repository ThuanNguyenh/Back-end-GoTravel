package com.gotravel.gotravel.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.auth.AuthenticationRequest;
import com.gotravel.gotravel.auth.AuthenticationResponse;
import com.gotravel.gotravel.auth.RegisterRequest;
import com.gotravel.gotravel.converter.UserConverter;
import com.gotravel.gotravel.entity.Role;
import com.gotravel.gotravel.entity.Token;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.RoleCustomRepository;
import com.gotravel.gotravel.repository.RoleRepository;
import com.gotravel.gotravel.repository.TokenRepository;
import com.gotravel.gotravel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;

	private final AuthenticationManager authenticationManager;

	private final RoleCustomRepository roleCustomRepository;

	private final JwtService jwtService;

	private final RoleRepository roleRepository;

	private final PasswordEncoder passwordEncoder;

	private final TokenRepository tokenRepository;

	private final UserConverter userConverter;

	// register
	public AuthenticationResponse register(RegisterRequest registerRequest) {

		var user = User.builder().userName(registerRequest.getUsername()).email(registerRequest.getEmail())
				.password(passwordEncoder.encode(registerRequest.getPassword())).build();

		// kiểm tra xem vai trò có được cung cấp hay không
		Role defaultRole;
		if (registerRequest.getRole() != null) {
			defaultRole = roleRepository.findByName(registerRequest.getRole().getName());

		} else {
			defaultRole = roleRepository.findByName("ROLE_USER");
		}

		// Gán vai trò cho user
		user.setRoles(Set.of(defaultRole));

		var savedUser = userRepository.save(user);

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority(defaultRole.getName()));

		var jwtToken = jwtService.generateToken(user, authorities);
		var jwtRefreshToken = jwtService.generateRefreshToken(user, authorities);

		saveUserToken(savedUser, jwtToken);

		return AuthenticationResponse.builder().token(jwtToken).refreshToken(jwtRefreshToken)
				.user(userConverter.toDTO(user)).build();

	}

	// login
	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
				authenticationRequest.getPassword()));

		// kiểm tra người dùng tồn tại
		User user = userRepository.findByEmail(authenticationRequest.getEmail())
				.orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

		// Lấy danh sách các vai trò từ repository
		List<Role> roles = roleCustomRepository.getRole(user);

		// tao mot tap hop moi chua cac vai tro da luu trong co so du lieu
		Set<Role> setRoles = new HashSet<>(roles);

		// tạo danh sách các quyền được cấp cho người dùng từ các vai trò đã lưu
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		setRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

		// tao jwtToken va refreshJwtToken
		var jwtToken = jwtService.generateToken(user, authorities);
		var jwtRefreshToken = jwtService.generateRefreshToken(user, authorities);

		// kiểm tra hạn sử dụng của token
//		if (jwtService.isTokenExpired(jwtToken)) {
//			throw new RuntimeException("Token đã hết hạn.");
//		}

		// thu hồi tất cả các token của người dùng và đảm bảo chỉ có một token hoạt động
		revokeAllUserTokens(user);

//		// Lưu token mới của người dùng
		saveUserToken(user, jwtToken);

		// Tra ve doi tuong AuthenticationRepository voi token va refreshToken
		return AuthenticationResponse.builder().token(jwtToken).refreshToken(jwtRefreshToken)
				.user(userConverter.toDTO(user)).build();

	}

	// LƯU TOKEN
	private void saveUserToken(User user, String jwtToken) {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = startTime.plusMinutes(60);

		var token = Token.builder().user(user).token(jwtToken).TokenType("BEARER").expired(false).revoked(false)
				.StartTime(startTime)
				.endTime(endTime)
				.build();
		tokenRepository.save(token);
	}

	// thu hồi tất cả các token
	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty()) {
			return;
		}

		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});

		tokenRepository.saveAll(validUserTokens);

	}

	// ĐĂNG XUẤT
	public void logout(String jwtToken) {
		// Tìm kiếm token trong csdl
		var storedToken = tokenRepository.findByToken(jwtToken).orElse(null);

		if (storedToken != null) {

			// Đặt trạng thái của token thành đã hết hạn và thu hồi
			storedToken.setExpired(true);
			storedToken.setRevoked(true);
			tokenRepository.save(storedToken);
		}
	}

}
