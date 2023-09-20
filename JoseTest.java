package pri.tjq.jose;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * @author tjq
 * @since 2023/9/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JoseTest.class)
public class JoseTest {

    public static final String HSBC_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjERDUUC0sfmFDfxEhPuDt1fQYg5akTMEWDDRMIDwtHUQnzojyUHmjG3FNKWtMPGlOHoDeVCflyBjEasUf94BX9CjBizJU9+PaSRl1st9nVuEzkvOszmekAQUSho5vVoJGxe5fPHJpzPZFhsTwO4iJdhsOzUmvvp366Rx2d3NxvQNgSW9HhCIq5E+AUA4TMI/vjz1UrbAZ0cjWnZbXUKooda7FS/yvQNauzI3h4Y7kEMV5Ve/PvuTqwL5vI6c7QoPc9m6xsZ/syRatNWqyEZkHNd3FJG3xk9B4aKTCLb7JjuUDlunN/3307oOIYY+ofERakWUplShY3d+1klAOWCgKwIDAQAB";
    public static final String HSBC_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCMRENRQLSx+YUN/ESE+4O3V9BiDlqRMwRYMNEwgPC0dRCfOiPJQeaMbcU0pa0w8aU4egN5UJ+XIGMRqxR/3gFf0KMGLMlT349pJGXWy32dW4TOS86zOZ6QBBRKGjm9WgkbF7l88cmnM9kWGxPA7iIl2Gw7NSa++nfrpHHZ3c3G9A2BJb0eEIirkT4BQDhMwj++PPVStsBnRyNadltdQqih1rsVL/K9A1q7MjeHhjuQQxXlV78++5OrAvm8jpztCg9z2brGxn+zJFq01arIRmQc13cUkbfGT0HhopMItvsmO5QOW6c3/ffTug4hhj6h8RFqRZSmVKFjd37WSUA5YKArAgMBAAECggEAJjKEvoDHdFwF3twe8LPpl6kjR0CvazEgcr/Ah9vlMM9cWnAjxK/cj8to08B2XIjBvJKFlajd/PLF587WA0g6yZPwgUL7BIsx66kMzqSVrZ95mBBO1sHPK1rhB5kCtkzpg+Y/DgZuKslUyc9P3s86n2HDf5Am4AUnWRVsJ69eVVpDiPmK4kA0gYTkDga7hBOShXkquePJcPeCq3PFviOL36Eu+/k3sparb15fP4CDNL3rgW9fZRSxCf7w6yKETL/3Ddkw2ax6yQTBJ3aczGJ6nHYRRawHse+sHXlqhMypL8IQy2EQhir3utTjkNo7ol8w1Dcin429UxJwdbalHubBcQKBgQC/LMv+kHP8eismN8JW52jSEsyCPgPa+I3PelsLvgGxiO/qlAeaKgKHS6zL/X+C/fY2fnTf0iePD7vxdULU9QNWkbIzbRSg5c9UA4JimLInKCxvg6lEpj0S81MsUl/vm4O7ftxpMgBv4XJrthtErgWKbfey5ex8xpcQW7yjzt54UwKBgQC71EybbWSuP/sp1UzNJQsSdGBlREGzqW9q3p91+seVugdS56hxpoyNSs2MRjmTPo6uGJGk8AESPOwFG3uVfOvFuV7YFMfhyvjRydBqo/pTUbHKGuihg8WdECtIYrm0TyECYbZpSDIll2pFFgBZbuFtFqtWFktbJuZDEjVboUpdyQKBgFKKfOlEZU+1T7wIhxgOgetxwmrZa8C3YEMciQLg3spQXYXitAWT7dOMkObiZJOiIxQUNVIN4paaLINsJZbc0rwl29LoXee6UXHssfbwBrtxVP4w0nyZafUsqdSRHGrPsmjZtUCWHiowJL7suYlFuY6zPCc5romRFNgzQ5dWUieRAoGAFPe6JY+ssZcK3ryk+jGsbr73E9buzrOXcIKxomYAacJ+ls6tiyJghlVXZNViEAHnw5+SgqsYM4UBhABxZ1qupz/uSojnV1llNWYmEgGELFDuW/VtCrq/EK2BZ28989bxhGaMCmy4zGF+x3+RQmqSV680KUQL3X0WL16U/kmfF6ECgYAP1bKsRvXNsv/G6CaoRyU4Lns3v0U2WufazynbBlwRF1SR5klRNsUaGKqBdx6dofqaZHeoC5kBu9lNa2aRYKfJpjxd/FTXt4z6eRl6bt+9h1S03rR9rqOt4r+wrS9VHKFGMDqqaeNIHN5PVQEZKZivkEIPOH4WYlhbLCVIMC9mIw==";
    public static final String ALI_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq8m+wcC1ZghbKD4r2K0PZBmIf+OsFqxnF0wXcgjaw54gr9waA7rQfC7wErqnFRa8ZWL6MKb2xzMJz+2SbS83FHUoyD5MHqm9Bm+EG+KIn07qhkXFzQh2Qj+5Jl/5mygbsmE6GSEzq2BbB4JYMDXZSz6kMJhFGz+ga7AulugRknabo+ichezVtt+XY9OSMHqfUD8HYs8D/74bkEZ86vmVMtgzFGPRzpU9E6Y7RHVNlZOjce74ZQmT1hsQZD3etuN2On/TpJzNLpZ8tU0olazg1bSqbkNMMXm/w8iCnBgn+IqPNfJjplt/mVY7xwqkfd/0gKhOd4jnkXJ0ZUlor87HkwIDAQAB";
    public static final String ALI_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCryb7BwLVmCFsoPivYrQ9kGYh/46wWrGcXTBdyCNrDniCv3BoDutB8LvASuqcVFrxlYvowpvbHMwnP7ZJtLzcUdSjIPkweqb0Gb4Qb4oifTuqGRcXNCHZCP7kmX/mbKBuyYToZITOrYFsHglgwNdlLPqQwmEUbP6BrsC6W6BGSdpuj6JyF7NW235dj05Iwep9QPwdizwP/vhuQRnzq+ZUy2DMUY9HOlT0TpjtEdU2Vk6Nx7vhlCZPWGxBkPd6243Y6f9OknM0ulny1TSiVrODVtKpuQ0wxeb/DyIKcGCf4io818mOmW3+ZVjvHCqR93/SAqE53iOeRcnRlSWivzseTAgMBAAECggEAb/FNPtCm5FA5pqWsyMstJluFGWFw+G1r2ECbXpHqjGJ0hmR1B00rImEry+iZjCJ+rcqrohyK3w+hKz9ylXeY+tnFs4b0715IcPMmMU2THcy9ArZgyNTkyxLfQeR0sOiX88trooiCNQDbEK+da/0LFf9B2vC+x5zUmpf6JBghq0BQ3MlJaMGQLVHtXEjZeoXO61BnegjohEFdOPtRiWu+IXSWZkqaYj01sRSrA37yRUuE0oDVNFa/2S+G+m4HR3y6vJnTq/XBYZP8XMTRJHFvU2kM1+sD0Tg+Q43eXaNE35r2yrGkyXrR4fXpxoxpxLHBBXhVoeM6vP15XJBtWCEk8QKBgQDiUzMkCosCsaxguJlrcIUp3R1LxJAZFr83F1FVBrM2uqn9Wi0jOmgoGOti4MfgP1fRRyFg+P1icDpMU1g5ur2sNzgH8YQdMj9mInUBljiJZpA3oAlcBPm0SLfBOk2RrQxn7Z6CDT5ZgVGRqwH0328w/A+e6pev6LiGfkGpncJqpwKBgQDCT/bz/el2L0G0QHbdZKrRpmaLcaUDHu9GJPW3WNXzi3p48bRVhRWet/w4bo055x8SHbmn1wNnaJdbSGltwTjNX60HW53UIumU3FRbvd8XrxCwbEcIHUYa+L6q5Mjn05SO8/iHUIj+QwkpdA4/OlXsFMqW0OcF51TP+lJdupkVNQKBgQC5hQvBumayoaO0cP1JxaVSaK7wAcNA3ZWGejWwJdrXvPFE/RtM6j6gx7SDi9ArsKCyqsTgsZk6jNYs5JDIWZvKxETIth/esuanOQ2CbHoRnyIOhr/FT0rEjmsaGEmnZRtrQauR3XtDcS7E4lGrQU/q7mN7l78ZcgF4NZxrhibstwKBgQCHWhJIpV8BfyuCdsEVuLCwdvvJOHFcOiksMESk4ZGvI0I9iwg15g1IURq06HUhl1fyU5hmTITHfHiqEke2Nzjs/i9WB8Bp66PPmKZ+fUUwhzbV3aahBVs3IHwYKGO18JDz/wfoofW7P1DbekcOTgFaAKWZ5ox2dUyDzvee6IMH3QKBgHGWw61hPhNP6k34ieNaJc6ropKc4L9g7l6jiwRqjLXxceBKpwdc2PDnDZjWzqQGzRvWYmLZazJlhZWmDCM3AmTKv7ELXRatIws5rQgDa0Y7PK4RS1XONpVO7WnRYQR3BnC78QyIngM36eDdVip7iLD0BDTuzKw2abjNUfDz/ezA";

    @Test
    public void generateRSAKey() throws Exception {
        RSAKeyGenerator generator = new RSAKeyGenerator(2048);
        RSAKey rsaKey = generator.generate();
        RSAPublicKey rsaPublicKey = rsaKey.toRSAPublicKey();
        RSAPrivateKey rsaPrivateKey = rsaKey.toRSAPrivateKey();
        String publicKey = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());

        System.out.println(publicKey);
        System.out.println("=====");
        System.out.println(privateKey);

    }

    @Test
    public void test() throws Exception {

        PublicKey hsbcPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(HSBC_PUBLIC_KEY)));
        PrivateKey hsbcPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(HSBC_PRIVATE_KEY)));
        PublicKey aliPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(ALI_PUBLIC_KEY)));
        PrivateKey aliPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(ALI_PRIVATE_KEY)));


        // 创建 JWS 头部，指定签名算法和 A 的私钥
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .build();

        // 创建 JWT 载荷
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("issuer")
                .subject("subject")
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))  // 设置过期时间为 5 分钟后
                .build();

        // 使用 A 的私钥对 JWT 载荷进行签名
        JWSSigner signerA = new RSASSASigner((RSAPrivateKey) hsbcPrivateKey);
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(signerA);

        // 获取 JWT 签名字符串
        String jwsSignature = signedJWT.serialize();
        System.out.println("=======================================");
        System.out.println(jwsSignature);




        // 创建 JWE 头部，指定加密算法和秘钥封装方法
        JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256CBC_HS512)
                .build();

        // 封装 JWT 签名字符串
        Payload payload = new Payload(jwsSignature);

        // 使用 B 的公钥对 JWE 进行加密
        RSAKey rsaPublicKeyB = new RSAKey.Builder((RSAPublicKey) aliPublicKey).build();
        RSAEncrypter encrypter = new RSAEncrypter(rsaPublicKeyB);
        JWEObject jweObject = new JWEObject(jweHeader, payload);
        jweObject.encrypt(encrypter);

        // 获取加密后的 JWT 字符串
        String jweToken = jweObject.serialize();
        System.out.println("=======================================");
        System.out.println(jweToken);
        // 假设发送给别人后，再收到数据
        // TODO +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // 使用 B 的私钥对 JWE 进行解密
        RSADecrypter decrypter = new RSADecrypter((RSAPrivateKey) aliPrivateKey);
        jweObject = JWEObject.parse(jweToken);
        jweObject.decrypt(decrypter);

        // 获取 JWS 签名字符串
        String receivedJwsSignature = jweObject.getPayload().toString();
        System.out.println("=======================================");
        System.out.println(receivedJwsSignature);
        // 使用 A 的公钥对 JWS 进行验证
        RSAKey rsaPublicKeyA = new RSAKey.Builder((RSAPublicKey) hsbcPublicKey).build();
        JWSVerifier verifierA = new RSASSAVerifier(rsaPublicKeyA);
        signedJWT = SignedJWT.parse(receivedJwsSignature);
        boolean isValid = signedJWT.verify(verifierA);

        if (isValid) {
            System.out.println("JWT 签名验证通过");
            System.out.println("Payload: " + signedJWT.getPayload().toString());
        } else {
            System.out.println("JWT 签名验证失败");
        }
    }

}
