package com.projteam.app.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.projteam.app.service.AccountService;

@SpringBootTest
public class AccountServiceTests
{
	@Autowired
	private AccountService accServ;
	
	@RepeatedTest(15)
	public void canInitializeAdminAccountMultipleTimes()
	{
		assertDoesNotThrow(() -> accServ.initAdminAccount());
	}
}
