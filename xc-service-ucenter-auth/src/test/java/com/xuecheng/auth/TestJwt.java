package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/13 19:03
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {

    //创建JWT令牌
    @Test
    public void testCreateJwt(){
        //密钥库的证书文件
        String keystore = "xc.keystore";

        //密钥库的密码
        String keystore_password = "xuechengkeystore";

        //密钥库文件路径
        ClassPathResource classPathResource = new ClassPathResource(keystore);

        //密钥的别名
        String alias = "xckey";

        //密钥的访问密码
        String key_password = "xuecheng";

        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource,keystore_password.toCharArray());
        //密钥对(公钥和私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //获取私钥
//        PrivateKey privateKey = keyPair.getPrivate();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //jwt令牌的内容
        Map<String,String> body = new HashMap<>();
        body.put("name","yibo");
        String bodyString = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(privateKey));
        //生成jwt令牌的编码
        String jwt_token = jwt.getEncoded();
        System.out.println(jwt_token);
    }

    //校验jwt
    @Test
    public void testVerify(){
        //公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";

        //jwt令牌
        String jwt_token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1Njg1MjU0MTMsImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wbGFuIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZSIsImNvdXJzZV9maW5kX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXIiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX21hcmtldCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcHVibGlzaCIsImNvdXJzZV9waWNfbGlzdCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6ImY3ZDQ3YzdmLTFjM2QtNDVmOC1hODQxLWZmNDI3YmNjZmU4NCIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.Wg7li5428bImRilc2r8oWAkth3ZCFJyZ9BlXTgo2MI4yH_jCK4wPpaz6tlUj2DdwJpOG2qD2QaJ-HpqAQmCSHyc3F2Ched0kBeKIXY5mW8G0Muqjp34YyS3VR-S3EqtV2rAWFJVaKN6qJnKbU817XUYZeIhOmiJu53eTcjvYrM9aieLNsdfw-5Nw3asFGsUubKTpxfjpSflEWHMu5dO8K6EkxIaZaUwMLEW1F8oD5-yg0ugffajQN6dVDd775lmR3bO3cPCOwLYOqdUy0DICVrpOLfeAHthQSDtq1NxA8IkcQtbEr7YjZOV2_3KnJuYum-m-U1uVB0NrNdwrIgp_mQ";

        //校验jwt令牌
        Jwt jwt = JwtHelper.decodeAndVerify(jwt_token, new RsaVerifier(publicKey));
        //拿到jwt中自定义的内容
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
