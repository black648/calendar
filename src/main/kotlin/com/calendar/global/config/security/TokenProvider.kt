package com.calendar.global.config.security

import com.calendar.domain.member.domain.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenProvider(
        private var secretKey: String = "hangry",
        private val tokenValidTime: Long = 30 * 60 * 1000L,
        private val redisTemplate: StringRedisTemplate? = null,
        private val userDetailsService: UserDetailsService? = null

) {
    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    //JWT 토큰 생성
    fun createToken(member: Member): String {
        val claims: Claims = Jwts.claims().setSubject(member.name()) //JWT payload에 저장되는 단위
        claims.put("role", member.role()) // 정보는 key / value 쌍으로 저장된다.
        val now = Date()
        val token: String = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(Date(now.time + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact()
        val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()
        valueOperations.set(token, member.toString()) // redis set 명령어
        return token
    }

    //JWT 토큰에서 인증 정보 조회
    fun getAuthentication(token: String?): Authentication {
        val userDetails = userDetailsService!!.loadUserByUsername(getUserToken(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    // 토큰에서 회원 정보 추출
    fun getUserToken(token: String?): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject()
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    fun resolveToken(request: HttpServletRequest): String {
        return request.getHeader("X-AUTH-TOKEN")
    }

    // 토큰의 유효성 + 만료일자 확인
    fun validateToken(jwtToken: String?): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
            val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()
            if (ObjectUtil.isEqualStr(CommonConstants.CONST_LOGOUT, valueOperations.get(jwtToken))) {
                log.info("로그아웃된 토큰 입니다.")
                return false
            }
            !claims.getBody().getExpiration().before(Date())
        } catch (e: Exception) {
            false
        }
    }
}
