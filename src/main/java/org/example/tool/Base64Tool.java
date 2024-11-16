package org.example.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Random;

@Component
public class Base64Tool {

    @Autowired
    private Random random;

    private String code;

    public String encode(String initStr) throws UnsupportedEncodingException {
        code = String.valueOf(random.nextInt(0,1000));
        System.out.println("code:"+code);
        initStr = code+initStr;
        byte[] encode1Byte = Base64.getEncoder().encode(initStr.getBytes("UTF-8"));
        byte[] encode2Byte = Base64.getEncoder().encode(encode1Byte);
        return new String(encode2Byte);
    }

    public String decode(String initStr) throws UnsupportedEncodingException {

        byte[] decode1Byte = Base64.getDecoder().decode(initStr.getBytes("UTF-8"));
        byte[] decode2Byte = Base64.getDecoder().decode(decode1Byte);
        String decodeTStr = new String(decode2Byte);
        Integer begin = decodeTStr.indexOf(code)+code.length();
        System.out.println("起始位置:"+begin);
        String encodeTStr = decodeTStr.substring(begin);
        return encodeTStr;
    }
}
