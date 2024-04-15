package com.eg.test.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityManager{
	
	 @Bean
     public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
         httpSecurity.csrf().disable()
                 .authorizeHttpRequests()
                 .anyRequest()
                 .authenticated()
                 .and()
                 .httpBasic();
         return httpSecurity.build();
     }
	 
	 @Bean
     public InMemoryUserDetailsManager userDetailsService() {
         UserDetails user = User.withDefaultPasswordEncoder()
                 .username("admin")
                 .password("admin123")
                 .roles("ADMIN")
                 .build();
         return new InMemoryUserDetailsManager(user);
     }

}
