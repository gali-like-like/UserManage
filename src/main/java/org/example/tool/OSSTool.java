package org.example.tool;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

public class OSSTool {

    public static boolean uploadFile(String objectFilePath,InputStream inputStream) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        }
        catch (ClientException e) {
            return false;
        }
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "user-headers";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
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
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectFilePath, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            if (ossClient != null) {
                ossClient.shutdown();
            }
            return false;
        }
        return true;
    }

    public static String getAccessUrl(String objectPath) throws ClientException {
        // 以华东1（杭州）的外网Endpoint为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "itst";
        // 填写Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        URL signedUrl = null;
        try {
            // 指定生成的签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Instant timestrampNow = Instant.now();
            Date expiration = new Date(timestrampNow.getEpochSecond()*1000 + 3600 * 1000L);
            // 生成签名URL。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectPath, HttpMethod.GET);
            // 设置过期时间。
            request.setExpiration(expiration);
            // 通过HTTP GET请求生成签名URL。
            signedUrl = ossClient.generatePresignedUrl(request);
            // 打印签名URL。
            System.out.println("signed url for getObject: " + signedUrl);
            return signedUrl.toString();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return null;
        } catch (com.aliyun.oss.ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return null;
        }
    }

}
