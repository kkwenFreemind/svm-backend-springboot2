package com.svm.backend.security.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.svm.backend.modules.ums.dto.PayloadParam;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.service.UmsAdminService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
/**
 * JwtToken生成的工具類
 * @author Kevin Chang
 *
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的類型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、創建時間、生成時間）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String CLAIM_KEY_USERNAME = "sub";

    private static final String CLAIM_KEY_CREATED = "created";

    private static final Integer DEFAULT_RERRESH_TIME = 30 * 60;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UmsAdminService adminService;

    /**
     * 根據负责生成JWT的token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从token中獲得JWT中的负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOGGER.info("JWT格式驗證失敗:{}", token);
        }

        return claims;
    }

    /**
     * 生成token的過期時間
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 由token中獲得登入用戶帳號名稱及統編
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);

            username = claims.getSubject();
            log.info("CLAINMS==>" + username);
            //CLAINMS==>{sub=kevin, created=1668498774897, invoice=70772123, exp=1669103574}
            log.info("CLAINMS==>" + claims.toString());

            String payload = claims.toString().replace("{", "").replace("}", "");

            String[] keyVals = payload.split(", ");
            HashMap<String, String> holder = new HashMap(keyVals.length);

            for (String keyVal : keyVals) {
                String[] parts = keyVal.split("=", 2);
                holder.put(parts[0], parts[1]);
            }
            log.info("Holder:" + holder);
            log.info("sub:" + holder.get("sub"));

        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 由token中獲得登入用戶帳號名稱及統編等資訊
     *
     * @param token
     * @return
     */
    public PayloadParam getPayloadFromToken(String token) {
        PayloadParam payloadParam = new PayloadParam();
        try {
            Claims claims = getClaimsFromToken(token);
            //CLAINMS==>{sub=kevin, created=1668498774897, invoice=70772123, exp=1669103574}
            log.info("CLAINMS==>" + claims.toString());

            String payload = claims.toString().replace("{", "").replace("}", "");

            String[] keyVals = payload.split(", ");
            HashMap<String, String> holder = new HashMap(keyVals.length);
            for (String keyVal : keyVals) {
                String[] parts = keyVal.split("=", 2);
                holder.put(parts[0], parts[1]);
            }
            payloadParam.setSub(holder.get("sub"));
            payloadParam.setExp(Long.valueOf(holder.get("exp")));
            payloadParam.setCreated(Long.valueOf(holder.get("created")));


        } catch (Exception e) {
            log.info(e.toString());
        }
        return payloadParam;
    }


    public UmsAdmin getCallerInfoFromToken(String token) {
        String username = getUserNameFromToken(token.substring(tokenHead.length()));
        log.info("token username:" + username);
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        return umsAdmin;
    }

    /**
     * 驗證token是否還有效
     *
     * @param token       客户端傳入的token
     * @param userDetails 從資料庫中查詢出来的用户資料
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        PayloadParam payloadParam = getPayloadFromToken(token);
        //String username = getUserNameFromToken(token);
        return payloadParam.getSub().equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已經失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从token中獲得過期時間
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 根據用户信息生成token
     */
    public String generateTokenByUserDetails(UserDetails userDetails) {
        int initLenght = 10;
        Map<String, Object> claims = new HashMap<>(initLenght);
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 當原来的token没過期時是可以刷新的
     *
     * @param oldToken 带tokenHead的token
     */
    public String refreshHeadToken(String oldToken) {
        if (StrUtil.isEmpty(oldToken)) {
            return null;
        }
        String token = oldToken.substring(tokenHead.length());
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        //token校驗不通過
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        //如果token已經過期，不支持刷新
        if (isTokenExpired(token)) {
            return null;
        }
        //如果token在30分鐘之内剛刷新過，返回原token
        if (tokenRefreshJustBefore(token, DEFAULT_RERRESH_TIME)) {
            return token;
        } else {
            claims.put(CLAIM_KEY_CREATED, new Date());
            return generateToken(claims);
        }
    }

    /**
     * 判断token在指定時間内是否剛剛刷新過
     *
     * @param token 原token
     * @param time  指定時間（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaimsFromToken(token);
        Date created = claims.get(CLAIM_KEY_CREATED, Date.class);
        Date refreshDate = new Date();
        //刷新時間在創建時間的指定時間内
        if (refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time))) {
            return true;
        }
        return false;
    }
}
