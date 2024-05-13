package com.ffood.g1.security;

import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/resources/**", "/templates/**", "/static/css/**", "/static/js/**", "/static/img/**", "/static/scss/**", "/static/vendors/**",
                        "/css/**", "/js/**", "/img/**", "/scss/**", "/vendors/**",
                        "/static/dashboard/**","/dashboard/**").permitAll()
                // Swagger config
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()

                .antMatchers("/resources/**", "/templates/**",
                        "/static/css/**", "/static/js/**", "/static/img/**", "/static/scss/**", "/static/vendors/**", "/static/dashboard/**",
                        "/css/**", "/js/**", "/img/**", "/scss/**", "/vendors/**", "/dashboard/**").permitAll()

                //Homepage
                .antMatchers("/").permitAll()

                //Common
                .antMatchers("/register/**", "/register", "/register/verify", "/change-password/**", "/change-password", "/now").permitAll()

                // Multiple role access with url
                // TODO: Need modify
                .antMatchers(
                        "/dashboard/**",
                        "/edit-staff-profile/**", "/edit-staff-profile",
                        "/staff-change-password/**", "/staff-change-password").hasAnyRole("ADMIN", "MANAGER", "MARKETING", "SALE")

                //ADMIN
                .antMatchers("/search-staff").hasRole("ADMIN")

                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll();
    }
}