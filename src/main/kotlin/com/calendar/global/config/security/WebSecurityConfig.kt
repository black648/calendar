package com.calendar.global.config.security

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@RequiredArgsConstructor
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    private val tokenProvider: TokenProvider? = null

    // 암호화에 필요한 PasswordEncoder 를 Bean 등록합니다.
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    // authenticationManager를 Bean 등록합니다.
    @Bean
    @Throws(Exception::class)
    fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    protected fun configure(httpSecurity: HttpSecurity) {
        //Override 된 confiure 메소드에서 "/admin/**", "/user/**" 형식의 URL로
        // 들어오는 요청에 대해 인증을 요구
        httpSecurity
                .httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                .csrf().disable() // csrf 보안 토큰 disable처리.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .and()
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/member/**").hasRole("USER")
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(AuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter::class.java)
        // AuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
    }
}