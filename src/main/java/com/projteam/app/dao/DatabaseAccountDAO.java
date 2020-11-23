package com.projteam.app.dao;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.projteam.app.domain.Account;

@Primary
@Repository("DatabaseAccountDAO")
@Transactional
public class DatabaseAccountDAO implements AccountDAO
{
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Account insertAccount(Account acc)
	{
		UUID id = acc.getId();
		if (id == null)
		{
			id = UUID.randomUUID();
			acc = acc.setId(id);
		}
		em.createNativeQuery("INSERT INTO Account "
				+ "(id, email, username, password, "
				+ "accountNonExpired, accountNonLocked, credentialsNonExpired, accountEnabled) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
			.setParameter(1, id)
			.setParameter(2, acc.getEmail())
			.setParameter(3, acc.getUsername())
			.setParameter(4, acc.getPassword())
			.setParameter(5, acc.isAccountNonExpired())
			.setParameter(6, acc.isAccountNonLocked())
			.setParameter(7, acc.isCredentialsNonExpired())
			.setParameter(8, acc.isEnabled())
			.executeUpdate();
		return acc;
	}
	@Override
	public void updateAccount(Account acc)
	{
		UUID id = acc.getId();
		String email = acc.getEmail();
		Objects.requireNonNull(id);
		Objects.requireNonNull(email);
		em.createQuery("UPDATE Account acc SET "
				+ "acc.username = :username"
				+ "acc.password = :password"
				+ "acc.accountNonExpired = :accountNonExpired"
				+ "acc.accountNonLocked = :accountNonLocked"
				+ "acc.credentialsNonExpired = :credentialsNonExpired"
				+ "acc.accountEnabled = :enabled"
				+ "WHERE acc.id = :id AND acc.email = :email")
			.setParameter("id", id)
			.setParameter("email", email)
			.setParameter("username", acc.getUsername())
			.setParameter("password", acc.getPassword())
			.setParameter("accountNonExpired", acc.isAccountNonExpired())
			.setParameter("accountNonLocked", acc.isAccountNonLocked())
			.setParameter("credentialsNonExpired", acc.isCredentialsNonExpired())
			.setParameter("enabled", acc.isEnabled())
			.executeUpdate();
	}
	@Override
	public void deleteAccount(UUID id)
	{
		em.createQuery("DELETE acc FROM Account WHERE acc.id = :id")
			.setParameter("id", id)
			.executeUpdate();
	}
	@Override
	public Account[] selectAccounts()
	{
		ArrayList<Account> ret = new ArrayList<>();
		for (Object o: 
			em.createQuery("SELECT acc FROM Account AS acc")
				.getResultList())
			ret.add((Account) o);
		return ret.toArray(l -> new Account[l]);
	}
	@Override
	public Account selectAccount(UUID id)
	{
		try
		{
			return (Account) em
					.createQuery("SELECT acc FROM Account AS acc WHERE acc.id = :id")
					.setParameter("id", id)
					.getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}
	}
	@Override
	public Account selectAccount(String email)
	{
		try
		{
			return (Account) em
					.createQuery("SELECT acc FROM Account AS acc WHERE acc.email = :email")
					.setParameter("email", email)
					.getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}
	}
}
