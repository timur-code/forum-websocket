package com.example.forumwebsocket.security;

import com.example.forumwebsocket.filter.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/user/login");
        http.csrf().disable();

        http.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/", true);

        http.authorizeRequests().antMatchers("/user/login/**", "/user/token/refresh/**", "/user/create/**", "/resources/**", "/login/**", "/").permitAll();

        http.authorizeRequests().antMatchers(GET, "/user/**").hasAnyAuthority("ROLE_USER");
        
        http.authorizeRequests().antMatchers(GET, "/admin/**").hasAnyAuthority("ROLE_ADMIN")
                .and().exceptionHandling().accessDeniedPage("/accessDenied");
        http.authorizeRequests().antMatchers(POST, "/admin/**").hasAnyAuthority("ROLE_ADMIN");

        http.authorizeRequests().antMatchers(GET, "/post/**").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers(POST, "/post/create").hasAnyAuthority("ROLE_USER");


        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
