package com.projteam.app.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccountTests
{
	@Test
	void testAccountConstructor()
	{
		assertDoesNotThrow(() -> new Account());
	}

	@Test
	void testIDfromBuilder()
	{
		UUID id = UUID.randomUUID();
		
		Account acc = new Account.Builder()
				.withID(id)
				.build();
		
		assertEquals(acc.getId(), id);
	}
	@Test
	void testIDfromSetter()
	{
		UUID id = UUID.randomUUID();
		
		Account acc = new Account();
		acc.setId(id);
		
		assertEquals(acc.getId(), id);
	}
	@Test
	void testGetEmailByBuilder()
	{
		String email = "email";
		
		Account acc = new Account.Builder()
				.withEmail(email)
				.build();
		
		assertEquals(acc.getEmail(), email);
	}

	@Test
	void testGetEmailBySetter()
	{
		String email = "email";
		
		Account acc = new Account();
		acc.setEmail(email);
		
		assertEquals(acc.getEmail(), email);
	}
	@Test
	void testEmailByBuilder()
	{
		String email = "email";
		
		Account acc = new Account.Builder()
				.withEmail(email)
				.build();
		
		assertEquals(acc.getEmail(), email);
	}
	@Test
	void testEmailBySetter()
	{
		String email = "email";
		
		Account acc = new Account();
		acc.setEmail(email);
		
		assertEquals(acc.getEmail(), email);
	}
	@Test
	void testUsernameByBuilder()
	{
		String username = "username";
		
		Account acc = new Account.Builder()
				.withUsername(username)
				.build();
		
		assertEquals(acc.getUsername(), username);
	}
	@Test
	void testUsernameBySetter()
	{
		String username = "username";
		
		Account acc = new Account();
		acc.setUsername(username);
		
		assertEquals(acc.getUsername(), username);
	}
	@Test
	void testNicknameByBuilder()
	{
		String nickname = "nickname";
		
		Account acc = new Account.Builder()
				.withNickname(nickname)
				.build();
		
		assertEquals(acc.getNickname(), nickname);
	}
	@Test
	void testNicknameBySetter()
	{
		String nickname = "nickname";
		
		Account acc = new Account();
		acc.setNickname(nickname);
		
		assertEquals(acc.getNickname(), nickname);
	}
	@Test
	void testPasswordHashByBuilder()
	{
		String passwordHash = "passHash";
		
		Account acc = new Account.Builder()
				.withPasswordHash(passwordHash)
				.build();
		
		assertEquals(acc.getPassword(), passwordHash);
	}
	@Test
	void testPasswordHashBySetter()
	{
		String passwordHash = "passHash";
		
		Account acc = new Account();
		acc.setPasswordHash(passwordHash);
		
		assertEquals(acc.getPassword(), passwordHash);
	}
	@Test
	void testRolesByBuilder()
	{
		List<String> roles = List.of("Role1", "role2", "r3");
		
		Account acc = new Account.Builder()
				.withRoles(roles)
				.build();
		
		assertEquals(acc.getRoles(), roles);
	}
	@Test
	void testRolesBySetter()
	{
		List<String> roles = List.of("Role1", "role2", "r3");
		
		Account acc = new Account();
		acc.setRoles(roles);
		
		assertEquals(acc.getRoles(), roles);
	}
	@Test
	void testEnabledByBuilder()
	{
		boolean enabled = true;
		
		Account acc = new Account.Builder()
				.enabled(enabled)
				.build();
		
		assertEquals(acc.isEnabled(), enabled);
	}
	@Test
	void testEnabledBySetter()
	{
		boolean enabled = true;
		
		Account acc = new Account();
		acc.setAccountEnabled(enabled);
		
		assertEquals(acc.isEnabled(), enabled);
	}
	@Test
	void testNonExpiredByBuilder()
	{
		boolean nonExpired = true;
		
		Account acc = new Account.Builder()
				.nonExpired(nonExpired)
				.build();
		
		assertEquals(acc.isAccountNonExpired(), nonExpired);
	}
	@Test
	void testNonExpiredBySetter()
	{
		boolean nonExpired = true;
		
		Account acc = new Account();
		acc.setAccountNonExpired(nonExpired);
		
		assertEquals(acc.isAccountNonExpired(), nonExpired);
	}
	@Test
	void testNonLockedByBuilder()
	{
		boolean nonLocked = true;
		
		Account acc = new Account.Builder()
				.nonLocked(nonLocked)
				.build();
		
		assertEquals(acc.isAccountNonLocked(), nonLocked);
	}
	@Test
	void testNonLockedBySetter()
	{
		boolean nonLocked = true;
		
		Account acc = new Account();
		acc.setAccountNonLocked(nonLocked);
		
		assertEquals(acc.isAccountNonLocked(), nonLocked);
	}
	@Test
	void testCredentialsNonExpiredByBuilder()
	{
		boolean credentialsNonExpired = true;
		
		Account acc = new Account.Builder()
				.credentialsNonExpired(credentialsNonExpired)
				.build();
		
		assertEquals(acc.isCredentialsNonExpired(), credentialsNonExpired);
	}
	@Test
	void testCredentialsNonExpiredBySetter()
	{
		boolean credentialsNonExpired = true;
		
		Account acc = new Account();
		acc.setCredentialsNonExpired(credentialsNonExpired);
		
		assertEquals(acc.isCredentialsNonExpired(), credentialsNonExpired);
	}
	
	@Test
	void shouldHaveAssignedRoles()
	{
		List<String> roles = List.of("Role1", "role2", "r3");
		
		Account acc = new Account.Builder()
				.withRoles(roles)
				.build();
		
		roles.forEach(role -> assertTrue(acc.hasRole(role)));
	}
	@Test
	void shouldNotHaveUnassignedRoles()
	{
		List<String> roles = List.of("Role1", "role2", "r3");
		
		Account acc = new Account.Builder()
				.withRoles(roles)
				.build();
		
		assertFalse(acc.hasRole("role4"));
	}
	@Test
	void shouldConvertRolesToAuthorities()
	{
		List<String> roles = List.of("Role1", "role2", "r3");
		
		Account acc = new Account.Builder()
				.withRoles(roles)
				.build();
		
		acc.getAuthorities().forEach(auth -> 
				assertTrue(roles.contains(auth.getAuthority())));
	}
	@Test
	void shouldConvertToString()
	{
		assertDoesNotThrow(() -> new Account().toString());
	}
	
	@Test
	void shouldBeEqualWhenSameEmptyObjectCompared()
	{
		Account acc = new Account();
		
		assertTrue(acc.equals(acc));
		assertEquals(acc.hashCode(), acc.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	void shouldBeEqualWhenSameObjectCompared(Account acc)
	{
		assertTrue(acc.equals(acc));
		assertEquals(acc.hashCode(), acc.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoEqualAccounts")
	void shouldBeEqualWhenEqualObjectsCompared(Account acc1, Account acc2)
	{
		assertTrue(acc1.equals(acc2));
		assertEquals(acc1.hashCode(), acc2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoUnequalAccounts")
	void shouldNotBeEqualWhenUnequalObjectsCompared(Account acc1, Account acc2)
	{
		assertFalse(acc1.equals(acc2));
	}
	
	//---Sources---
	
	public static List<Arguments> mockAccount()
	{
		return List.of(
				Arguments.of(new Account.Builder()
						.withEmail("testAcc@test.pl")
						.withUsername("TestAccount")
						.withPasswordHash("QWERTY")
						.build()));
	}
	public static List<Arguments> mockTwoEqualAccounts()
	{
		String email = "testAcc@test.pl";
		String username = "TestAccount";
		String pass = "QWERTY";
		List<String> roles = List.of("role1", "r2", "Role3", "role-4", "Role-5");
		
		return List.of(
				Arguments.of(new Account.Builder()
							.withEmail(email)
							.withUsername(username)
							.withPasswordHash(pass)
							.withRoles(roles)
							.build(),
						new Account.Builder()
							.withEmail(email)
							.withUsername(username)
							.withPasswordHash(pass)
							.withRoles(roles)
							.build()));
	}
	public static List<Arguments> mockTwoUnequalAccounts()
	{
		String email1 = "testAcc1@test1.pl";
		String email2 = "testAcc2@test2.pl";
		String email3 = "testAcc2@test3.pl";
		String username1 = "TestAccount1";
		String username2 = "TestAccount2";
		String username3 = "TestAccount3";
		String pass1 = "QWERTY1";
		String pass2 = "QWERTY2";
		String pass3 = "QWERTY3";
		List<String> roles1 = List.of("role1", "r2");
		List<String> roles2 = List.of("role1", "role2");
		List<String> roles3 = List.of("r1", "role2", "Role3");
		
		return List.of(
				Arguments.of(new Account.Builder()
						.withEmail(email1)
						.withUsername(username1)
						.withPasswordHash(pass1)
						.withRoles(roles1)
						.build(),
					new Account.Builder()
						.withEmail(email2)
						.withUsername(username2)
						.withPasswordHash(pass2)
						.withRoles(roles2)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email1)
						.withUsername(username1)
						.withPasswordHash(pass1)
						.withRoles(roles1)
						.build(),
					new Account.Builder()
						.withEmail(email3)
						.withUsername(username3)
						.withPasswordHash(pass3)
						.withRoles(roles3)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email2)
						.withUsername(username2)
						.withPasswordHash(pass2)
						.withRoles(roles2)
						.build(),
					new Account.Builder()
						.withEmail(email3)
						.withUsername(username3)
						.withPasswordHash(pass3)
						.withRoles(roles3)
						.build()),
				Arguments.of(new Account(),
					new Account.Builder()
						.withEmail(email1)
						.withUsername(username1)
						.withPasswordHash(pass1)
						.withRoles(roles1)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email2)
						.withUsername(username2)
						.withPasswordHash(pass2)
						.withRoles(roles2)
						.build(),
					new Account()),
				Arguments.of(new Account.Builder()
						.withRoles(roles1)
						.build(),
					new Account.Builder()
						.withRoles(roles2)
						.build()),
				Arguments.of(new Account.Builder()
						.withRoles(roles1)
						.build(),
					new Account.Builder()
						.withRoles(roles3)
						.build()),
				Arguments.of(new Account.Builder()
						.withRoles(roles2)
						.build(),
					new Account.Builder()
						.withRoles(roles3)
						.build()));
	}
}