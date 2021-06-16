package com.projteam.competico.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import com.projteam.competico.service.AccountService;

@SpringBootTest
@ContextConfiguration(name = "Integration-tests")
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
