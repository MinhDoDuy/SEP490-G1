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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class    SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

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
                .antMatchers("/resources/**", "/templates/**", "/static/**",
                        "/css/**", "/js/**", "/img/**", "/scss/**", "/vendors/**",
                        "/dashboard/**", "/register").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/register/**", "/register", "/register/verify", "/change-password/**", "/change-password").permitAll()
                .antMatchers("/", "/login/**","/login", "/homepage/**", "/canteens/**", "/canteen_details", "/canteen_contact", "/food_details").permitAll()
                .antMatchers("/add_to_cart").permitAll()
                .antMatchers("/view-profile/**", "/update-profile", "/staff-change-password/**", "/staff-change-password")
                .hasAnyRole("ADMIN", "MANAGER", "STAFF", "CUSTOMER")
                .antMatchers("/search-staff", "/dashboard/", "/manage-user/**",
                        "/edit-profile/**", "/edit-user/**", "/add-user/**",
                        "/manage-canteen/**", "/add-canteen", "/search-canteen",
                        "/edit-canteen/**", "/delete-canteen", "/dashboard-admin")
                .hasRole("ADMIN")
                .antMatchers("/manage-staff/**", "/search-staff", "/add-staff/**",
                        "/edit-staff/**", "/canteen-details/**", "/canteen/update-canteen/**",
                        "/canteen/view-canteen/**","/manage-food/**","/manage-food","/add-food", "edit-food")
                .hasRole("MANAGER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/homepage", true)
                .failureUrl("/login?error")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER");
    }

}