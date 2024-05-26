package com.ffood.g1.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "user_phone")
	private String userPhone;



	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Feedback> feedbacks = Collections.emptySet(); // Initialize as empty set

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Cart> carts = Collections.emptySet(); // Initialize as empty set

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Orders> orders = Collections.emptySet(); // Initialize as empty set

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (role == null) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new SimpleGrantedAuthority(role.getRoleName()));
	}

	@Override
	public String getUsername() {
		return email;  // Assuming email is used as username
	}

	public String getUserName() {
		return userName;
	}

	public Integer getUserId() {
		return userId;
	}

	@Override
	public String getPassword() {
		return password;
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
