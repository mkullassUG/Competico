package com.projteam.app.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.service.AccountService;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(name = "Integration-tests")
@AutoConfigureMockMvc
public class LoginTestsWithRealSecurityContext
{
	private MockMvc mvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private AccountService accServ;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@BeforeEach
	public void setUpMockMvc()
	{
		mvc = MockMvcBuilders
		    .webAppContextSetup(webApplicationContext)
		    .apply(sharedHttpSession())
		    .build();
	}
	
	@BeforeAll
	public void initAccounts()
	{
		registrationDTO().forEach(regDto -> accServ.register(null, regDto, false));
	}
	
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
	@MethodSource("mockLoginDTO")
	public void canAuthorizeCorrectly(LoginDTO logDto) throws JsonProcessingException, Exception
	{
		mvc.perform(get("/api/v1/authenticated/"))
			.andDo(res -> res.getRequest()
					.getSession()
					.removeAttribute(HttpSessionSecurityContextRepository
							.SPRING_SECURITY_CONTEXT_KEY));
		
		mvc.perform(post("/api/v1/login/")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(logDto)))
			.andExpect(status().isOk());
		
		mvc.perform(get("/api/v1/authenticated/"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}
	
	//---Helpers---
	
	public String toJson(Object o) throws JsonProcessingException
	{
		return mapper.writeValueAsString(o);
	}
	public static List<RegistrationDTO> registrationDTO()
	{
		String email1 = "testPlayerForLogin1@test.pl";
		String email2 = "testPlayerForLogin2@test.pl";
		String username1 = "TestPlayerForLogin1";
		String username2 = "TestPlayerForLogin2";
		
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
	
	public static List<Arguments> mockLoginDTO()
	{
		return registrationDTO().stream()
			.map(regDto -> new LoginDTO(regDto.getEmail(), regDto.getPassword()))
			.distinct()
			.map(logDto -> Arguments.of(logDto))
			.collect(Collectors.toList());
	}
}
