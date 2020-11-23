package com.projteam.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.projteam.app.service.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	private DaoAuthenticationProvider authProvider;
	
	public SecurityConfig(DaoAuthenticationProvider authProvider)
	{
		this.authProvider = authProvider;
	}
	
	@Override
	public void configure(HttpSecurity sec) throws Exception
	{
		//TODO test
		sec
			.authorizeRequests()
			.antMatchers(
		                "/js/**",
		                "/css/**",
		                "/assets/**",
		                "/login",
		                "/register",
		                "/api/v1/login/",
		                "/api/v1/register/")
			.permitAll()
			.antMatchers("/actuator/**").hasRole("ACTUATOR_ADMIN")
			.anyRequest().authenticated()
			
//			.and()
//			.formLogin()
//			.usernameParameter("email")
//			.passwordParameter("password")
//			.loginPage("/login")
//			.defaultSuccessUrl("/dashboard")
//			.permitAll()

			.and()
			.logout()
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/")
			.permitAll()
			.and()
			.exceptionHandling()
			.authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
			.and()
			.csrf().disable(); //TODO remove and implement properly client-side
	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.authenticationProvider(authProvider);
	}
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}
}
@Configuration
class PasswordEncoderConfig
{
	@Primary
	@Bean
	@Qualifier("bCryptPasswordEncoder")
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}
}
@Configuration
class AuthenticationProviderConfig
{
	private AccountService accServ;
	private PasswordEncoder encoder;
	
	@Autowired
	public AuthenticationProviderConfig(AccountService accServ,
			@Qualifier("bCryptPasswordEncoder") PasswordEncoder encoder)
	{
		this.accServ = accServ;
		this.encoder = encoder;
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider()
	{
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(accServ);
		auth.setPasswordEncoder(encoder);
		return auth;
	}
}
