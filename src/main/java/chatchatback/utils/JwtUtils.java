package chatchatback.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String SECRET_KEY = "6K+X6Z+15pm65a+76aG555uu5b+F5oiQ5ZOI5ZOI5ZOI5ZOI"; // 秘钥, 诗韵智寻项目必成哈哈哈哈
    private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000; //有效时长

    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()   //创建JWT构建器对象
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // 使用HMAC-SHA算法将字符串密钥转换为安全密钥对象
                .claims(claims)  // 新版API使用，携带自定义信息
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  //设置过期时间（当前时间+预设的过期时长）
                .compact();
    }

    public static Claims parseJwt(String token) {
        return Jwts.parser()    //创建JWT解析器对象
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // 秘钥
                .build()    // 构建解析器实例
                .parseSignedClaims(token)  // 解析并验证JWT签名
                .getPayload();  // 获取JWT自定义内容
    }
}
