package com.gotravel.gotravel.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@Column(name = "id", unique = true, updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Tour> tours;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Like> likes;

	@Column(name = "user_name", columnDefinition = "NTEXT")
	private String userName;

	@Column(name = "email", nullable = false, unique = true)
	String email;

	@Column(name = "password", nullable = false)
	String password;

	@Column(name = "phone")
	String phone;

	@Column(name = "province", columnDefinition = "NTEXT")
	String province;

	@Column(name = "district", columnDefinition = "NTEXT")
	String district;

	@Column(name = "ward", columnDefinition = "NTEXT")
	String ward;

	String avatar;

	Date createdAt;

	Date updatedAt;

	// config

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Token> tokens;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "users_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))

//	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Role> roles = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		roles.stream().forEach(i -> authorities.add(new SimpleGrantedAuthority(i.getName())));
		return List.of(new SimpleGrantedAuthority(authorities.toString()));
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
