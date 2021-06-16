package com.projteam.competico.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static com.projteam.competico.domain.Account.LECTURER_ROLE;
import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.competico.config.SecurityContextConfig;
import com.projteam.competico.domain.Account;
import com.projteam.competico.dto.RegistrationDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.testutils.Holder;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTests
{
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private SecurityContextConfig secConf;
	
	@Autowired
	private AccountService accServ;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	
	@Test
	public void contextLoads() throws Exception
	{
		assertNotNull(mvc);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationDTO")
	public void canAuthorizeCorrectly(RegistrationDTO regDto) throws JsonProcessingException, Exception
	{
		SecurityContext secCon = mock(SecurityContext.class, withSettings().serializable());
		
		Holder<Authentication> authHolder = new Holder<>();
		
		when(secCon.getAuthentication()).thenAnswer(authHolder.getterAnswer());
		doAnswer(authHolder.setterAnswer()).when(secCon).setAuthentication(any());
		
		when(secConf.getContext()).thenReturn(secCon);
		
		assertFalse(accServ.isAuthenticated());
		mvc.perform(post("/api/v1/register/")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(regDto)))
			.andExpect(status().isCreated());
		assertTrue(accServ.isAuthenticated());
		
		assertTrue(accServ.isAuthenticated());
		Account account = accServ.getAuthenticatedAccount().orElse(null);
		assertNotNull(account);
		assertEquals(account.getEmail(), regDto.getEmail());
		assertEquals(account.getUsername(), regDto.getUsername());
		assertTrue(account.hasRole(regDto.isPlayer()?PLAYER_ROLE:LECTURER_ROLE));
	}
	
	//---Helpers---
	
	public String toJson(Object o) throws JsonProcessingException
	{
		return mapper.writeValueAsString(o);
	}
	public static List<RegistrationDTO> registrationDTO()
	{
		String email1 = "testPlayerForRegistration1@test.pl";
		String email2 = "testPlayerForRegistration2@test.pl";
		String username1 = "TestPlayerForRegistration1";
		String username2 = "TestPlayerForRegistration2";
		
		String password = "QWERTYuiop123";
		
		return List.of(
				new RegistrationDTO(
						email1,
						username1,
						password,
						true),
				new RegistrationDTO(
						email2,
						username2,
						password,
						false));
	}
	
	//---Sources---
	
	public static List<Arguments> mockRegistrationDTO()
	{
		return registrationDTO().stream()
				.map(regDto -> Arguments.of(regDto))
				.collect(Collectors.toList());
	}
}