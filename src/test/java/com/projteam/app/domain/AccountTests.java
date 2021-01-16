package com.projteam.app.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
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
				.withPassword(passwordHash)
				.build();
		
		assertEquals(acc.getPassword(), passwordHash);
	}
	@Test
	void testPasswordHashBySetter()
	{
		String passwordHash = "passHash";
		
		Account acc = new Account();
		acc.setPassword(passwordHash);
		
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
		acc.setEnabled(enabled);
		
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
		assertDoesNotThrow(() -> acc1.hashCode());
		assertDoesNotThrow(() -> acc2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	void shouldNotBeEqualWhenComparedToNull(Account acc)
	{
		assertFalse(acc.equals(null));
		assertDoesNotThrow(() -> acc.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	void shouldNotBeEqualWhenComparedToDifferentType(Account acc)
	{
		assertFalse(acc.equals(new Object()));
		assertDoesNotThrow(() -> acc.hashCode());
	}
	
	//---Sources---
	
	public static List<Arguments> mockAccount()
	{
		return List.of(
				Arguments.of(new Account.Builder()
						.withEmail("testAcc@test.pl")
						.withUsername("TestAccount")
						.withPassword("QWERTY")
						.build()));
	}
	public static List<Arguments> mockTwoEqualAccounts()
	{
		UUID id = UUID.randomUUID();
		String email = "testAcc@test.pl";
		String username = "TestAccount";
		String nickname = "TestAccountNickname";
		String pass = "QWERTY";
		List<String> roles = List.of("role1", "r2", "Role3", "role-4", "Role-5");
		List<String> nullRoles = list(null, null, null, null);
		
		return List.of(
				Arguments.of(new Account.Builder()
							.withEmail(email)
							.withUsername(username)
							.withPassword(pass)
							.withRoles(roles)
							.build(),
						new Account.Builder()
							.withEmail(email)
							.withUsername(username)
							.withPassword(pass)
							.withRoles(roles)
							.build()),
				//Null 1 arg
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build()),
				//Null 2 args
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.build()),
				//Null 3 args
				Arguments.of(new Account.Builder()
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.build()),
				//All null args
				Arguments.of(new Account.Builder().build(),
					new Account.Builder().build()),
				//Null roles
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(nullRoles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(nullRoles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//Null role args
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(null)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(null)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()));
	}
	public static List<Arguments> mockTwoUnequalAccounts()
	{
		UUID id = UUID.randomUUID();
		UUID otherId = UUID.randomUUID();
		String email = "testAcc1@test1.pl";
		String otherEmail = "testAcc2@test2.pl";
		String thirdEmail = "testAcc2@test3.pl";
		String username = "TestAccount1";
		String otherUsername = "TestAccount2";
		String thirdUsername = "TestAccount3";
		String nickname = "TestAccountNickname1";
		String otherNickname = "TestAccountNickname2";
		String pass = "QWERTY1";
		String otherPass = "QWERTY2";
		String thirdPass = "QWERTY3";
		List<String> roles = List.of("role1", "r2");
		List<String> otherRoles = List.of("role1", "role2");
		List<String> thridRoles = List.of("r1", "role2", "Role3");
		List<String> nullRoles = list(null, null);
		
		return List.of(
				//All args different
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withUsername(otherUsername)
						.withPassword(otherPass)
						.withRoles(otherRoles)
						.build()),
				//One arg different
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(otherUsername)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(otherPass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(otherRoles)
						.build()),
				//One null arg
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				//Null email
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(otherUsername)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withPassword(otherPass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withPassword(pass)
						.withRoles(otherRoles)
						.build()),
				//Null username
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withPassword(otherPass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.withRoles(otherRoles)
						.build()),
				//Null password
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withUsername(username)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(otherUsername)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withRoles(otherRoles)
						.build()),
				//Null roles
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withUsername(username)
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(otherUsername)
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(otherPass)
						.build()),
				//Null email and username
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withPassword(otherPass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withPassword(pass)
						.withRoles(otherRoles)
						.build()),
				//Null email and password
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(otherUsername)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withUsername(username)
						.withRoles(otherRoles)
						.build()),
				//Null email and roles
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withRoles(otherRoles)
						.build()),
				//Null username and password
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withRoles(otherRoles)
						.build()),
				//Null username and roles
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.withPassword(pass)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withEmail(email)
						.withPassword(otherPass)
						.build()),
				//Null password and roles
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withPassword(otherPass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withPassword(pass)
						.withRoles(otherRoles)
						.build()),
				//Null 3 arguments
				Arguments.of(new Account.Builder()
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withRoles(otherRoles)
						.build()),
				Arguments.of(new Account.Builder()
						.withPassword(pass)
						.build(),
					new Account.Builder()
						.withPassword(otherPass)
						.build()),
				Arguments.of(new Account.Builder()
						.withUsername(username)
						.build(),
					new Account.Builder()
						.withUsername(otherUsername)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.build(),
					new Account.Builder()
						.withEmail(otherEmail)
						.build()),
				//Other
				Arguments.of(new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withEmail(thirdEmail)
						.withUsername(thirdUsername)
						.withPassword(thirdPass)
						.withRoles(thridRoles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(otherEmail)
						.withUsername(otherUsername)
						.withPassword(otherPass)
						.withRoles(otherRoles)
						.build(),
					new Account.Builder()
						.withEmail(thirdEmail)
						.withUsername(thirdUsername)
						.withPassword(thirdPass)
						.withRoles(thridRoles)
						.build()),
				Arguments.of(new Account(),
					new Account.Builder()
						.withEmail(email)
						.withUsername(username)
						.withPassword(pass)
						.withRoles(roles)
						.build()),
				Arguments.of(new Account.Builder()
						.withEmail(otherEmail)
						.withUsername(otherUsername)
						.withPassword(otherPass)
						.withRoles(otherRoles)
						.build(),
					new Account()),
				Arguments.of(new Account.Builder()
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withRoles(otherRoles)
						.build()),
				Arguments.of(new Account.Builder()
						.withRoles(roles)
						.build(),
					new Account.Builder()
						.withRoles(thridRoles)
						.build()),
				Arguments.of(new Account.Builder()
						.withRoles(otherRoles)
						.build(),
					new Account.Builder()
						.withRoles(thridRoles)
						.build()),
				//Full arguments
				//ID
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(otherId)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(null)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(null)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//Email
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(otherEmail)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(null)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(null)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//Username
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(otherUsername)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(null)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(null)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//Nickname
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(otherNickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(null)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(null)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//Password
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(otherPass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(null)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(null)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//Roles
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(otherRoles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(null)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(null)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//isEnabled
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(false)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//isAccoundNonExpired
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(false)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				//isAccountNonLocked
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(false)
						.credentialsNonExpired(true)
						.build()),
				//isCredentialsNonExpired
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(false)
						.build()),
				//Null roles
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(nullRoles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()),
				Arguments.of(new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(nullRoles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build(),
					new Account.Builder()
						.withID(id)
						.withEmail(email)
						.withUsername(username)
						.withNickname(nickname)
						.withPassword(pass)
						.withRoles(roles)
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build()));
	}
	
	@SafeVarargs
	private static <T> List<T> list(T... elements)
	{
		List<T> ret = new ArrayList<>();
		for (T elem: elements)
			ret.add(elem);
		return ret;
	}
}