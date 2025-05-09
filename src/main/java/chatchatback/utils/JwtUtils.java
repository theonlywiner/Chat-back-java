package chatchatback.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    public static String generateJwt(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        return Jwts.builder()   //创建JWT构建器对象
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8)) //
                .claims(claims)  // 新版API使用，携带自定义信息
                .setExpiration(new Date(System.currentTimeMillis() + ttlMillis))  //设置过期时间（当前时间+预设的过期时长）
                .compact();
    }
    public static Claims parseJwt(String secretKey, String token) {
        return Jwts.parser()    //创建JWT解析器对象
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))// 秘钥
                .build()    // 构建解析器实例
                .parseSignedClaims(token)  // 解析并验证JWT签名
                .getPayload();  // 获取JWT自定义内容
    }
}
