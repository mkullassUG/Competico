package com.projteam.app.dao;

import java.util.UUID;
import com.projteam.app.domain.Account;

public interface AccountDAO
{
	public Account insertAccount(Account acc);
	public void updateAccount(Account acc);
	public void deleteAccount(UUID id);
	public Account[] selectAccounts();
	public Account selectAccount(UUID id);
	public Account selectAccount(String email);
}
