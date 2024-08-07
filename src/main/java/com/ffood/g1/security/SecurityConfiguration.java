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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

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

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage("/403");
        return accessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Cho phép tất cả mọi người truy cập vào các URL này
                .antMatchers("/resources/**", "/templates/**", "/static/**",
                        "/css/**", "/js/**", "/img/**", "/scss/**", "/vendors/**",
                        "/dashboard/**", "/register","/forgot-password","/reset-password/**","/terms-of-service-and-privacy-policy").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/register/**", "/register", "/register/verify", "/change-password/**", "/change-password").permitAll()
                .antMatchers("/", "/login/**","/login", "/homepage/**", "/canteens/**", "/canteen_details", "/canteen_info", "/food_details","/update_cart_quantity").permitAll()
                .antMatchers("/add_to_cart","/foodByCategory/{categoryId}","/assign-confirm","/assign-confirm/**","/cart/**","/cart/payment","/cart/remove-from-cart-provisional").permitAll()
                // Các quyền truy cập yêu cầu xác thực
                .antMatchers("/view-profile/**", "/update-profile", "/staff-change-password/**", "/staff-change-password",
                        "/submit-feedback/**","/feedback-system-form/**",
                        "/vn/**","/submitOrder/**","/now/**","/vnpay-payment/**")
                .hasAnyRole("ADMIN", "MANAGER", "STAFF", "CUSTOMER")
                // Quyền cho ADMIN
                .antMatchers("/search-staff", "/dashboard/", "/manage-user/**",
                        "/edit-profile/**", "/edit-user/**", "/add-user/**",
                        "/manage-canteen/**", "/add-canteen", "/search-canteen",
                        "/edit-canteen/**", "/delete-canteen", "/dashboard-admin",
                        "/assign-manager-form/**","/check-email-manager/**","/assign-manager-confirm/**")
                .hasRole("ADMIN")
                // Quyền cho MANAGER
                .antMatchers("/manage-staff/**", "/search-staff", "/add-staff/**",
                        "/edit-staff/**", "/canteen-details/**", "/canteen/update-profile-canteen/**",
                        "/canteen/edit-profile-canteen/**","/manage-food/**","/manage-food","/canteen/**",
                        "/add-food-form", "/add-food-form/**", "/add-food", "/add-food/**","/check-email/**",
                        "/assign-staff-form/**","/assign-confirm/**", "/manage-category/**",
                        "/add-category-form", "/add-category", "/edit-category/**",
                        "dashboard-manager/**",
                        "/manage-feedback","/approve-feedback/**","/reject-feedback/**","/bulk-approve-feedback/**","/bulk-reject-feedback/**",
                        "/search-food/**","/add-food-form/**","/add-food","/edit-food/**",
                        "/add-quantity/**","/order-list/**","/update-order-status/**",
                        "/bulk-assign-orders/**","/reject-order/**"
                        )
                .hasRole("MANAGER")
                // Quyền cho STAFF
                .antMatchers("/order-list-ship/**","/complete-order/**", "/create-order-at-couter")
                .hasRole("STAFF")
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
                .permitAll()
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER");
    }
}
