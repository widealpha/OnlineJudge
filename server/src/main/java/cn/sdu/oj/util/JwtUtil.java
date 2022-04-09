package cn.sdu.oj.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author kmh
 */
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET; //JWT签证密钥
    private final String ROLE = "ROLE"; //Jwt中携带的身份key
    private final String USER_ID = "USER_ID"; //Jwt中携带的用户ID的key
    public final Long EXPIRATION = 60 * 60 * 24 * 7L; //过期时间7天，单位秒
    public final String TOKEN_HEADER = "Authorization"; //Header标识JWT
    public final String TOKEN_PREFIX = "Bearer "; //JWT标准开头,注意空格

    /**
     * 创建JWT
     *
     * @param username 账户名
     * @param userId   用户ID
     * @param roles    用户角色,以英文逗号(,)分隔开
     * @return 创建好的Token
     */
    public String createToken(String username, Integer userId, String roles) {
        assert username != null && userId != null && roles != null;
        Map<String, Object> map = new HashMap<>();
        map.put(ROLE, roles);
        map.put(USER_ID, userId);
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, SECRET)
                .setClaims(map).setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .compact();
    }

    /**
     * 根据token获取用户名
     *
     * @param token JWT
     * @return 用户名
     */
    public String getUsername(String token) {
        try {
            return getTokenBody(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Token载体信息
     *
     * @param token JWT
     * @return token携带的claim
     */
    private Claims getTokenBody(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * 获取token携带的用户角色列表
     *
     * @param token JWT
     * @return 用户角色, 以英文逗号(, )分隔开
     */
    public String getUserRole(String token) {
        return (String) getTokenBody(token).get(ROLE);
    }

    /**
     * 获取token携带的用户ID
     *
     * @param token JWT
     * @return 用户ID
     */
    public Integer getUserId(String token) {
        return (Integer) getTokenBody(token).get(USER_ID);
    }

    /**
     * 判断token是否过期
     *
     * @param token JWT
     * @return 是否过期
     */
    public Boolean isExpiration(String token) {
        try {
            return getTokenBody(token).getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    /**
     * 获取签发日期
     *
     * @param token JWT
     * @return 签发Token的日期
     */
    public Date getIssuedAt(String token) {
        return getTokenBody(token).getIssuedAt();
    }
}
