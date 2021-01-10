package com.projteam.app.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class Account implements UserDetails
{
	private @Id @Column(name = "id", unique = true) UUID id;
	private @Column(name = "email", unique = true) String email;
	private @Column(name = "username", unique = true) String username;
	private @Column(name = "password") String passwordHash;
	private @Column(name = "nickname") String nickname;
	
	private @Column(name = "accountEnabled") boolean accEnabled = true;
	private @Column(name = "accountNonExpired") boolean accNonExpired = true;
	private @Column(name = "accountNonLocked") boolean accNonLocked = true;
	private @Column(name = "credentialsNonExpired") boolean credNonExpired = true;
	
	@Column(name = "roles")
	@ElementCollection
	private List<String> roles = new ArrayList<>(Collections.emptyList());
	
	public static final String PLAYER_ROLE = "PLAYER";
	public static final String LECTURER_ROLE = "LECTURER";
	
	public Account()
	{}
	
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
	public List<String> getRoles()
	{
		return roles;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	public void setRoles(List<String> roles)
	{
		this.roles = roles;
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
		else if (!listsEqual(roles, other.roles))
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
	
	public void setAccountEnabled(boolean accEnabled)
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
	
	private <T> boolean listsEqual(List<T> l1, List<T> l2)
	{
		if (l1.size() != l2.size())
			return false;
		Iterator<T> it1 = l1.iterator();
		Iterator<T> it2 = l2.iterator();
		while (it1.hasNext())
		{
			if (!it1.next().equals(it2.next()))
				return false;
		}
		return true;
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
		public Builder withPasswordHash(String passwordHash)
		{
			acc.setPasswordHash(passwordHash);
			return this;
		}
		public Builder enabled(boolean accEnabled)
		{
			acc.setAccountEnabled(accEnabled);
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
}