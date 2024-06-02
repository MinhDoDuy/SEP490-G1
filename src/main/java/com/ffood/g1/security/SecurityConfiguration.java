package com.ffood.g1.security;

import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
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

    @Autowired
    private StringToLocalDateConverter stringToLocalDateConverter;

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
                        "/static/dashboard/**","/dashboard/**","/register").permitAll()
                // Swagger config
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/register/**", "/register", "/register/verify", "/change-password/**", "/change-password").permitAll()
                //.antMatchers("/", "/login/**").permitAll()
                //Homepage
                .antMatchers("/canteens","/homepage","/items_in_all_shop","/canteen_contact").permitAll()
                // Profile
                .antMatchers("/view-profile/","/update-profile",
                        "/staff-change-password/**", "/staff-change-password")
                .hasAnyRole("ADMIN", "MANAGER", "STAFF", "CUSTOMER")
                .antMatchers( "/static/dashboard/**","/dashboard/**","/register","/forgot-password","/reset-password","/view-profile/","/update-profile","/change-password").permitAll()
                // Swagger config
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/register/**", "/register", "/register/verify", "/change-password/**", "/change-password", "/homepage","/items_in_all_shop").permitAll()
                // Profile
                .antMatchers("/view-profile/","/update-profile",
                        "/staff-change-password/**", "/staff-change-password")
                .hasAnyRole("ADMIN", "MANAGER", "STAFF", "CUSTOMER")
                // Admin
                .antMatchers("/search-staff", "/dashboard/").hasRole("ADMIN")
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

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToLocalDateConverter);
    }
}
