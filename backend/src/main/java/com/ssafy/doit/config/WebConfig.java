package com.ssafy.doit.config;

import com.ssafy.doit.service.user.UserDetailsServiceImpl;
import com.ssafy.doit.service.jwt.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**").permitAll()
                .antMatchers("/user/signup").permitAll()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/checkNick").permitAll()
                .antMatchers("/user/checkEmail").permitAll()
                .antMatchers("/user/confirmEmail").permitAll()
                .antMatchers("/user/sendChangePwEmail").permitAll()
                .antMatchers("/user/confirmPw").permitAll()
                .antMatchers("/user/sendSignupEmail").permitAll()
                .antMatchers("/user/confirmSignup").permitAll()
                .antMatchers("/data/wordCloud").permitAll()
                .antMatchers("/data/rankingGroup").permitAll()
                .antMatchers("/group/searchGroup").permitAll()
                .antMatchers("/group/categoryGroup").permitAll()
                .antMatchers("/product/*").permitAll()
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().hasAnyRole("USER", "ADMIN")
                .and()
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl);
    }
}
