package org.example.tool;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
@Slf4j
public class JwtTool {

    private static SecretKey key;
    static {
        String secertString = "hello,world";
        byte[] keyBytes256 = new byte[32];
        int encodeNum = Base64.getEncoder().encode(secertString.getBytes(),keyBytes256);
        key = Keys.hmacShaKeyFor(keyBytes256);
    }

    public static String getJwt(HashMap<String, Object> peopleMap) {
        try {
            String jwt = Jwts.builder()
                    .setClaims(peopleMap)
                    .setExpiration(new Date(System.currentTimeMillis()+3600*1000))
                    .signWith(key)
                    .compact();
            log.info(jwt);
            return jwt;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, Object> parseJwt(String jwt) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        log.info("解析成功");
        // 将Claims对象转换为Map
        HashMap<String, Object> claimMap = new HashMap<>();
        claims.forEach((key, value) -> claimMap.put(key, value));
        return claimMap;
    }

}
