package com.projteam.app.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.projteam.app.domain.Account;

//Mock DAO for testing. DO NOT use in production.
@Repository("InMemoryAccountDAO")
public class InMemoryAccountDAO implements AccountDAO
{
	private ArrayList<Account> localStorage = new ArrayList<>();
	
	@Override
	public Account insertAccount(Account acc)
	{
		UUID id = acc.getId();
		String email = acc.getEmail();
		if (id != null)
		{
			for (Account a: localStorage)
			{
				if (a.getId().equals(id) || a.getEmail().equals(email))
					throw new IllegalArgumentException("Account already in database");
			}
			localStorage.add(acc);
			return acc;
		}
		else
		{
			for (Account a: localStorage)
			{
				if (a.getEmail().equals(email))
					throw new IllegalArgumentException("Account already in database");
			}
			
			while (true)
			{
				id = UUID.randomUUID();
				for (Account a: localStorage)
				{
					if (a.getId().equals(id))
						continue;
				}
				acc = acc.setId(id);
				localStorage.add(acc);
				return acc;
			}
		}
	}
	@Override
	public void updateAccount(Account acc)
	{
		UUID id = acc.getId();
		String email = acc.getEmail();
		if (id == null)
			throw new NullPointerException("Account's UUID is null.");
		for (Iterator<Account> iter = localStorage.iterator(); iter.hasNext();)
		{
			Account a = iter.next();
			if (a.getId().equals(id) && a.getEmail().equals(email))
			{
				iter.remove();
				localStorage.add(acc);
				return;
			}
		}
		throw new IllegalArgumentException("Account does not exist in the database.");
	}
	@Override
	public void deleteAccount(UUID id)
	{
		if (id == null)
			throw new NullPointerException("UUID is null.");
		for (Iterator<Account> iter = localStorage.iterator(); iter.hasNext();)
		{
			Account a = iter.next();
			if (a.getId().equals(id))
			{
				iter.remove();
				return;
			}
		}
	}
	@Override
	public Account[] selectAccounts()
	{
		return localStorage.toArray(l -> new Account[l]);
	}
	@Override
	public Account selectAccount(UUID id)
	{
		for (Account a: localStorage)
		{
			if (a.getId().equals(id))
				return a;
		}
		return null;
	}
	@Override
	public Account selectAccount(String email)
	{
		for (Account acc: localStorage)
		{
			if (acc.getEmail().equals(email))
				return acc;
		}
		return null;
	}
}
