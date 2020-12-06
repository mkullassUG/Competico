package com.projteam.app.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@IdClass(AccountID.class)
public class Account implements UserDetails
{
	private @Id @Column(name = "id", unique = true) UUID id;
	private @Id @Column(name = "email", unique = true) String email;
	private @Id @Column(name = "username", unique = true) String username;
	private @Column(name = "password") String passwordHash;
	private @Column(name = "nickname") String nickname;
	
	private @Column(name = "accountEnabled") boolean accEnabled;
	private @Column(name = "accountNonExpired") boolean accNonExpired;
	private @Column(name = "accountNonLocked") boolean accNonLocked;
	private @Column(name = "credentialsNonExpired") boolean credNonExpired;
	
	private @Column(name = "roles") @ElementCollection(fetch = FetchType.EAGER) List<String> roles;
	
	public static final String PLAYER_ROLE = "PLAYER";
	public static final String LECTURER_ROLE = "LECTURER";
	
	public Account()
	{
		accEnabled = true;
		accNonExpired = true;
		accNonLocked = true;
		credNonExpired = true;
		roles = new ArrayList<>();
	}
	public Account(String email, String username, String passwordHash)
	{
		this(null, email, username, passwordHash,
				true, true, true, true, new ArrayList<>());
	}
	public Account(UUID id, String email, String username, String passwordHash)
	{
		this(id, email, username, passwordHash,
				true, true, true, true, new ArrayList<>());
	}
	public Account(String email, String username, String passwordHash, List<String> roles)
	{
		this(null, email, username, passwordHash,
				true, true, true, true, roles);
	}
	public Account(UUID id, String email, String username, String passwordHash, List<String> roles)
	{
		this(id, email, username, passwordHash,
				true, true, true, true, roles);
	}
	public Account(String email, String username, String passwordHash,
			boolean accEnabled, boolean accNonExpired, boolean accNonLocked, boolean credNonExpired)
	{
		this(null, email, username, passwordHash,
				accEnabled, accNonExpired, accNonLocked, credNonExpired, new ArrayList<>());
	}
	public Account(UUID id, String email, String username, String passwordHash,
			boolean accEnabled, boolean accNonExpired, boolean accNonLocked, boolean credNonExpired)
	{
		this(id, email, username, passwordHash,
				accEnabled, accNonExpired, accNonLocked, credNonExpired, new ArrayList<>());
	}
	public Account(String email, String username, String passwordHash,
			boolean accEnabled, boolean accNonExpired, boolean accNonLocked, boolean credNonExpired,
			List<String> roles)
	{
		this(null, email, username, passwordHash,
				accEnabled, accNonExpired, accNonLocked, credNonExpired, roles);
	}
	public Account(UUID id, String email, String username, String passwordHash,
			boolean accEnabled, boolean accNonExpired, boolean accNonLocked, boolean credNonExpired,
			List<String> roles)
	{
		this.id = id;
		this.email = email;
		this.username = username;
		this.passwordHash = passwordHash;
		nickname = username;
		this.accEnabled = accEnabled;
		this.accNonExpired = accNonExpired;
		this.accNonLocked = accNonLocked;
		this.credNonExpired = credNonExpired;
		this.roles = new ArrayList<>(roles);
	}
	
	public UUID getId()
	{
		return id;
	}
	public String getEmail()
	{
		return email;
	}
	public String getUsername()
	{
		return username;
	}
	@Override
	public String getPassword()
	{
		return passwordHash;
	}
	public String getNickname()
	{
		return nickname;
	}
	
	public Account setId(UUID id)
	{
		return new Account(id, email, username, passwordHash);
	}
	public Account setEmail(String email)
	{
		return new Account(id, email, username, passwordHash);
	}
	public Account setUsername(String username)
	{
		return new Account(id, email, username, passwordHash);
	}
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	public List<String> getRoles()
	{
		return roles;
	}
	public void setRoles(List<String> roles)
	{
		this.roles = new ArrayList<>(roles);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (accEnabled ? 1231 : 1237);
		result = prime * result + (accNonExpired ? 1231 : 1237);
		result = prime * result + (accNonLocked ? 1231 : 1237);
		result = prime * result + (credNonExpired ? 1231 : 1237);
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((passwordHash == null) ? 0 : passwordHash.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
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
		if (accEnabled != other.accEnabled)
			return false;
		if (accNonExpired != other.accNonExpired)
			return false;
		if (accNonLocked != other.accNonLocked)
			return false;
		if (credNonExpired != other.credNonExpired)
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
		if (passwordHash == null)
		{
			if (other.passwordHash != null)
				return false;
		}
		else if (!passwordHash.equals(other.passwordHash))
			return false;
		if (roles == null)
		{
			if (other.roles != null)
				return false;
		}
		else if (!roles.equals(other.roles))
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
	public String toString()
	{
		return "[Account id="
				+ id + ", email="
				+ email + ", username="
				+ username + ", passwordHash="
				+ passwordHash + ", nickname="
				+ nickname + ", accEnabled="
				+ accEnabled + ", accNonExpired="
				+ accNonExpired + ", accNonLocked="
				+ accNonLocked + ", credNonExpired="
				+ credNonExpired + ", roles="
				+ roles + "]";
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return new ArrayList<>(roles.stream()
				.map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList()));
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return accNonExpired;
	}
	@Override
	public boolean isAccountNonLocked()
	{
		return accNonLocked;
	}
	@Override
	public boolean isCredentialsNonExpired()
	{
		return credNonExpired;
	}
	@Override
	public boolean isEnabled()
	{
		return accEnabled;
	}
	
	public void setEnabled(boolean accEnabled)
	{
		this.accEnabled = accEnabled;
	}
	public void setAccountNonExpired(boolean accNonExpired)
	{
		this.accNonExpired = accNonExpired;
	}
	public void setAccountNonLocked(boolean accNonLocked)
	{
		this.accNonLocked = accNonLocked;
	}
	public void setCredentialsNonExpired(boolean credNonExpired)
	{
		this.credNonExpired = credNonExpired;
	}
	
	public boolean hasRole(String role)
	{
		return roles.contains(role);
	}
}