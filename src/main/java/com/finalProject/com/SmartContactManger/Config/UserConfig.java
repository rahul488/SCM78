package com.finalProject.com.SmartContactManger.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.finalProject.com.SmartContactManger.Service.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class UserConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserServiceImpl userService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService);
	}
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
			
			http.authorizeRequests()
			.antMatchers("/users/**").hasRole("USER")
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/**").permitAll()
			.and()
			.formLogin().loginPage("/signin")
			.loginProcessingUrl("/authenticateUser")
			.defaultSuccessUrl("/users/home")
			.permitAll();
			
	}
	
	
	
}
