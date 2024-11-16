package org.example;

import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.example.tool.Base64Tool;
import org.example.tool.JwtTool;
import org.example.tool.SMSTool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;

import javax.sql.DataSource;

@SpringBootTest
@Slf4j
class UserManageApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SMSTool smsTool;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void contextLoads() {
    }

    @Test
    void createAndParseJwt() throws InterruptedException {
        Thread thread1 = new Thread(()->{
            HashMap<String,Object> items = new HashMap<>();
            items.put("id",1);
            items.put("name","张三");
            items.put("age",20);
            String jwt = JwtTool.getJwt(items);
            log.info("jwt:{}",jwt);
        });
        thread1.start();
        thread1.join();
    }

    @Test
    @DisplayName("测试数据源")
    public void testDataSource() {
        if (dataSource instanceof HikariDataSource) {
            log.info("这是德鲁伊数据源");
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            log.info("username:{},password:{},url:{}",hikariDataSource.getUsername(),hikariDataSource.getPassword(),hikariDataSource.getJdbcUrl());
        }
    }

    @Test
    void testRedisTemplate() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        HashMap<String,Object> items = new HashMap<>();
//        items.put("id",1);
//        items.put("name","张三");
//        items.put("age",20);
//        String jwt = JwtTool.getJwt(items);
//        valueOperations.set("id",jwt,60, TimeUnit.SECONDS);
        String id = (String)valueOperations.get("id");
        log.info("id:{}",id);
//        assertEquals(id,jwt);
    }

    @Test
    void testUploadFile() throws com.aliyuncs.exceptions.ClientException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "user-headers";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "headers/a.txt";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "H:\\Test\\a.txt";
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
        String region = "cn-beijing";

        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在");
        }
        finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Test
    public void download() throws com.aliyuncs.exceptions.ClientException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        //从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "examplebucket";
        // 填写不包含Bucket名称在内的Object完整路径，例如testfolder/exampleobject.txt。
        String objectName = "testfolder/exampleobject.txt";
        // 填写Object下载到本地的完整路径。
        String pathName = "D:\\localpath\\examplefile.txt";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(pathName));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Test
    public void getOSSFileUrl() throws com.aliyuncs.exceptions.ClientException {
        // 以华东1（杭州）的外网Endpoint为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "itst";
        // 填写Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "empManage/emp.sql";

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        // 设置请求头。
        Map<String, String> headers = new HashMap<String, String>();
        /*// 指定Object的存储类型。
        headers.put(OSSHeaders.STORAGE_CLASS, StorageClass.Standard.toString());
        // 指定ContentType。
        headers.put(OSSHeaders.CONTENT_TYPE, "text/txt");*/

        // 设置用户自定义元数据。
        Map<String, String> userMetadata = new HashMap<String, String>();
        /*userMetadata.put("key1","value1");
        userMetadata.put("key2","value2");*/

        URL signedUrl = null;
        try {
            // 指定生成的签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000L);

            // 生成签名URL。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
            // 设置过期时间。
            request.setExpiration(expiration);

            // 将请求头加入到request中。
            request.setHeaders(headers);
            // 添加用户自定义元数据。
            request.setUserMetadata(userMetadata);

            // 通过HTTP PUT请求生成签名URL。
            signedUrl = ossClient.generatePresignedUrl(request);
            // 打印签名URL。
            System.out.println("signed url for putObject: " + signedUrl);

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        /**
         * 标准答案
         * https://itst.oss-cn-beijing.aliyuncs.com/empManage/emp.sql?
         * Expires=1729783512&OSSAccessKeyId=TMP.3Kdzm7fLWs8aw2xMTkskw4cdZn9eEq4hoJTWvYwsMACqzq7etdp8EfQFfo2VLtDJF8K36xpZWATD6kuZ5vnDMVc2t38Lra&Signature=3RcZVmG4HDmhUKkMD9wOd5haKL0%3D
         *
         *
         * */
    }

    @Test
    @DisplayName("发送短信")
    public void testSend() throws ExecutionException, InterruptedException {
        smsTool.send("13312914106");
    }

    @Test
    @DisplayName("判断是否存在cachemange")
    public void testCache() {
        System.out.println(cacheManager.getCacheNames());
    }

    @Autowired
    private Base64Tool tool;

    @Test
    @DisplayName("加密解密")
    public void testEncodeDecoder() throws Exception {
        String encodeTStr = tool.encode("456789");
        System.out.println("加密后:"+encodeTStr);
        String decodeTStr = tool.decode(encodeTStr);
        System.out.println("解密后:"+decodeTStr);
    }

}
