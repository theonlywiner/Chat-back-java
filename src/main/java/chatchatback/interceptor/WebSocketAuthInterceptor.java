package chatchatback.interceptor;

import chatchatback.constant.JwtClaimsConstant;
import chatchatback.properties.JwtProperties;
import chatchatback.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        log.info("WebSocket beforeHandshake 方法被调用...");
        log.info("WebSocket 握手请求: {}", request.getURI());

        // 从查询参数中获取 token
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            log.info("WebSocket 请求中没有 token");
            return false;
        }

        // 提取 token
        String token = extractTokenFromQuery(query);
        log.info("拿到 token：{}", token);

        if (token == null) {
            log.info("WebSocket 请求头中没有 token");
            return false;
        }

        // 解析 token
        try {
            Claims claims = JwtUtils.parseJwt(jwtProperties.getAdminSecretKey(), token);
            Integer userId = (Integer) claims.get(JwtClaimsConstant.USER_ID);
            log.info("当前登录用户 userId 为：{}", userId);

            // 将用户ID存入 attributes，供 WebSocketHandler 使用
            attributes.put("userId", userId);

        } catch (Exception e) {
            log.info("WebSocket 解析令牌失败");
            return false;
        }

        log.info("WebSocket 令牌解析成功, 放行");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("WebSocket afterHandshake 方法被调用...");

        if (exception == null) {
            log.info("WebSocket 握手完成: {}", request.getURI());
        } else {
            log.error("WebSocket 握手异常: {}", exception.getMessage());
        }
    }

    /**
     * 从查询字符串中提取 token
     */
    private String extractTokenFromQuery(String query) {
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("token=")) {
                return param.substring(6);
            }
        }
        return null;
    }
}