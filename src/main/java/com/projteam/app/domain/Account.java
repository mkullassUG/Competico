package com.projteam.app.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.projteam.app.utils.Initializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Access(AccessType.FIELD)
@Table(name = "Account", uniqueConstraints =
	@UniqueConstraint(columnNames = {"email", "username"}))
public class Account implements UserDetails, Initializable
{
	private @Id @Column(name = "id", unique = true, updatable = false) UUID id;
	private @Column(name = "email", unique = true, updatable = true) String email;
	private @Column(name = "username", unique = true, updatable = false) String username;
	private @Column(name = "password", updatable = true) String password;
	private @Column(name = "nickname", updatable = true) String nickname;
	
	private @Column(name = "accountEnabled", updatable = true) boolean enabled = true;
	private @Column(name = "accountNonExpired", updatable = true) boolean accountNonExpired = true;
	private @Column(name = "accountNonLocked", updatable = true) boolean accountNonLocked = true;
	private @Column(name = "credentialsNonExpired", updatable = true) boolean credentialsNonExpired = true;
	
	@ElementCollection
	@Column(name = "roles", updatable = true)
	private List<String> roles;
	
	public static final String PLAYER_ROLE = "PLAYER";
	public static final String LECTURER_ROLE = "LECTURER";
	public static final String ACTUATOR_ADMIN = "ACTUATOR_ADMIN";
	public static final String SWAGGER_ADMIN = "SWAGGER_ADMIN";
	public static final String TASK_DATA_ADMIN = "TASK_DATA_ADMIN";
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (enabled != other.enabled)
			return false;
		if (accountNonExpired != other.accountNonExpired)
			return false;
		if (accountNonLocked != other.accountNonLocked)
			return false;
		if (credentialsNonExpired != other.credentialsNonExpired)
			return false;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (nickname == null)
		{
			if (other.nickname != null)
				return false;
		}
		else if (!nickname.equals(other.nickname))
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		
		if (listsUnequal(roles, other.roles))
			return false;
		
		if (username == null)
		{
			if (other.username != null)
				return false;
		}
		else if (!username.equals(other.username))
			return false;
		return true;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + (accountNonExpired ? 1231 : 1237);
		result = prime * result + (accountNonLocked ? 1231 : 1237);
		result = prime * result + (credentialsNonExpired ? 1231 : 1237);
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return new ArrayList<>(roles.stream()
				.map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList()));
	}
	
	public boolean hasRole(String role)
	{
		return roles.contains(role);
	}
	
	private <T> boolean listsUnequal(List<T> l1, List<T> l2)
	{
		if (l1 == null)
			return l2 != null;
		else if (l2 == null)
			return true;
		
		if (l1.size() != l2.size())
			return true;
		Iterator<T> it2 = l2.iterator();
		for (T elem1: l1)
		{
			T elem2 = it2.next();
			if (elem1 == null)
			{
				if (elem2 != null)
					return true;
			}
			else if (elem2 == null)
				return true;
			else if (!elem1.equals(elem2))
				return true;
		}
		return false;
	}
	
	public static class Builder
	{
		private Account acc;
		
		public Builder()
		{
			acc = new Account();
		}
		
		public Builder withID(UUID id)
		{
			acc.setId(id);
			return this;
		}
		public Builder withUsername(String username)
		{
			acc.setUsername(username);
			return this;
		}
		public Builder withEmail(String email)
		{
			acc.setEmail(email);
			return this;
		}
		public Builder withNickname(String nickname)
		{
			acc.setNickname(nickname);
			return this;
		}
		public Builder withPassword(String password)
		{
			acc.setPassword(password);
			return this;
		}
		public Builder enabled(boolean accEnabled)
		{
			acc.setEnabled(accEnabled);
			return this;
		}
		public Builder nonExpired(boolean accNonExpired)
		{
			acc.setAccountNonExpired(accNonExpired);
			return this;
		}
		public Builder nonLocked(boolean accNonLocked)
		{
			acc.setAccountNonLocked(accNonLocked);
			return this;
		}
		public Builder credentialsNonExpired(boolean credNonExpired)
		{
			acc.setCredentialsNonExpired(credNonExpired);
			return this;
		}
		public Builder withRoles(List<String> roles)
		{
			acc.setRoles(roles);
			return this;
		}
		
		public Account build()
		{
			return acc;
		}
	}

	@Override
	public void initialize()
	{
		Initializable.initialize(roles);
	}
}