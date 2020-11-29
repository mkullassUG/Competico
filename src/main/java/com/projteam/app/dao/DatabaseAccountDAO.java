package com.projteam.app.dao;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.projteam.app.domain.Account;

@Primary
@Repository("DatabaseAccountDAO")
@Transactional
public class DatabaseAccountDAO implements AccountDAO
{
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	@Override
	public Account insertAccount(Account acc)
	{
		UUID id = acc.getId();
		if (id == null)
		{
			id = UUID.randomUUID();
			acc = acc.setId(id);
		}
		
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(acc);
		em.getTransaction().commit();
		em.close();
		return acc;
	}
	@Override
	public void updateAccount(Account acc)
	{
		UUID id = acc.getId();
		String email = acc.getEmail();
		Objects.requireNonNull(id);
		Objects.requireNonNull(email);
		EntityManager em = emf.createEntityManager();
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
		em.close();
	}
	@Override
	public void deleteAccount(UUID id)
	{
		Account acc = selectAccount(id);
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.remove(acc);
		em.getTransaction().commit();
		em.close();
//		em.createQuery("DELETE acc FROM Account WHERE acc.id = :id")
//			.setParameter("id", id)
//			.executeUpdate();
	}
	@Override
	public Account[] selectAccounts()
	{
		EntityManager em = emf.createEntityManager();
		ArrayList<Account> ret = new ArrayList<>();
		for (Object o: 
			em.createQuery("SELECT acc FROM Account AS acc")
				.getResultList())
			ret.add((Account) o);
		em.close();
		return ret.toArray(l -> new Account[l]);
	}
	@Override
	public Account selectAccount(UUID id)
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			Account ret = (Account) em
					.createQuery("SELECT acc FROM Account AS acc WHERE acc.id = :id")
					.setParameter("id", id)
					.getSingleResult();
			em.close();
			return ret;
		}
		catch (NoResultException e)
		{
			em.close();
			return null;
		}
	}
	@Override
	public Account selectAccount(String email)
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			Account ret = (Account) em
					.createQuery("SELECT acc FROM Account AS acc WHERE acc.email = :email")
					.setParameter("email", email)
					.getSingleResult();
			em.close();
			return ret;
		}
		catch (NoResultException e)
		{
			em.close();
			return null;
		}
	}
}
