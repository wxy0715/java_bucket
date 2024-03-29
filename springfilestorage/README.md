官网:https://spring-file-storage.xuyanwu.cn/#/%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8

# 简介

在 SpringBoot 中通过简单的方式将文件存储到 本地、FTP、SFTP、WebDAV、谷歌云存储、阿里云OSS、华为云OBS、七牛云Kodo、腾讯云COS、百度云 BOS、又拍云USS、MinIO、 AWS S3、金山云 KS3、美团云 MSS、京东云 OSS、天翼云 OOS、移动云 EOS、沃云 OSS、 网易数帆 NOS、Ucloud US3、青云 QingStor、平安云 OBS、首云 OSS、IBM COS、其它兼容 S3 协议的平台

# 配置

```xml
<dependencies>
    <!-- spring-file-storage 必须要引入 -->
    <dependency>
        <groupId>cn.xuyanwu</groupId>
        <artifactId>spring-file-storage</artifactId>
        <version>0.5.0</version>
    </dependency>

    <!-- 华为云 OBS 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.huaweicloud</groupId>
        <artifactId>esdk-obs-java</artifactId>
        <version>3.22.3.1</version>
    </dependency>

    <!-- 阿里云 OSS 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.aliyun.oss</groupId>
        <artifactId>aliyun-sdk-oss</artifactId>
        <version>3.15.1</version>
    </dependency>

    <!-- 七牛云 Kodo 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.qiniu</groupId>
        <artifactId>qiniu-java-sdk</artifactId>
        <version>7.11.0</version>
    </dependency>

    <!-- 腾讯云 COS 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.qcloud</groupId>
        <artifactId>cos_api</artifactId>
        <version>5.6.98</version>
    </dependency>

    <!-- 百度云 BOS 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.baidubce</groupId>
        <artifactId>bce-java-sdk</artifactId>
        <version>0.10.218</version>
    </dependency>

    <!-- 又拍云 USS 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.upyun</groupId>
        <artifactId>java-sdk</artifactId>
        <version>4.2.3</version>
    </dependency>

    <!-- MinIO 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
        <version>8.4.3</version>
    </dependency>

    <!-- AWS S3 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-java-sdk-s3</artifactId>
        <version>1.12.272</version>
    </dependency>

    <!-- FTP 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>commons-net</groupId>
        <artifactId>commons-net</artifactId>
        <version>3.8.0</version>
    </dependency>

    <!-- SFTP 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.jcraft</groupId>
        <artifactId>jsch</artifactId>
        <version>0.1.55</version>
    </dependency>

    <!--糊涂工具类扩展，如果要使用 FTP、SFTP 则必须引入，否则不用引入-->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-extra</artifactId>
        <version>5.8.5</version>
    </dependency>

    <!-- WebDAV 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.github.lookfirst</groupId>
        <artifactId>sardine</artifactId>
        <version>5.10</version>
    </dependency>

    <!-- 谷歌云 Google Cloud Storage-->
    <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>google-cloud-storage</artifactId>
        <version>2.14.0</version>
    </dependency>

    <!--因 guava 存在较多冲突版本导致谷歌云存储无法使用，故引入独立版本-->
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>31.1-jre</version>
    </dependency>
    
</dependencies>
```

```xml
 </dependency>

    <!-- WebDAV 不使用的情况下可以不引入 -->
    <dependency>
        <groupId>com.github.lookfirst</groupId>
        <artifactId>sardine</artifactId>
        <version>5.10</version>
    </dependency>

    <!-- 谷歌云 Google Cloud Storage-->
    <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>google-cloud-storage</artifactId>
        <version>2.14.0</version>
    </dependency>

    <!--因 guava 存在较多冲突版本导致谷歌云存储无法使用，故引入独立版本-->
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>31.1-jre</version>
    </dependency>
    
</dependencies>
```

`application.yml`配置文件中添加以下相关配置（不使用的平台可以不配置）

```yaml
spring:
  file-storage: #文件存储配置
    default-platform: local-1 #默认使用的存储平台
    thumbnail-suffix: ".min.jpg" #缩略图后缀，例如【.min.jpg】【.png】
    local: # 本地存储（不推荐使用），不使用的情况下可以不写
      - platform: local-1 # 存储平台标识
        enable-storage: true  #启用存储
        enable-access: true #启用访问（线上请使用 Nginx 配置，效率更高）
        domain: "" # 访问域名，例如：“http://127.0.0.1:8030/test/file/”，注意后面要和 path-patterns 保持一致，“/”结尾，本地存储建议使用相对路径，方便后期更换域名
        base-path: D:/Temp/test/ # 存储地址
        path-patterns: /test/file/** # 访问路径，开启 enable-access 后，通过此路径可以访问到上传的文件
    local-plus: # 本地存储升级版，不使用的情况下可以不写
      - platform: local-plus-1 # 存储平台标识
        enable-storage: true  #启用存储
        enable-access: true #启用访问（线上请使用 Nginx 配置，效率更高）
        domain: "" # 访问域名，例如：“http://127.0.0.1:8030/”，注意后面要和 path-patterns 保持一致，“/”结尾，本地存储建议使用相对路径，方便后期更换域名
        base-path: local-plus/ # 基础路径
        path-patterns: /** # 访问路径
        storage-path: D:/Temp/ # 存储路径
    huawei-obs: # 华为云 OBS ，不使用的情况下可以不写
      - platform: huawei-obs-1 # 存储平台标识
        enable-storage: false  # 启用存储
        access-key: ??
        secret-key: ??
        end-point: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：http://abc.obs.com/
        base-path: hy/ # 基础路径
    aliyun-oss: # 阿里云 OSS ，不使用的情况下可以不写
      - platform: aliyun-oss-1 # 存储平台标识
        enable-storage: false  # 启用存储
        access-key: ??
        secret-key: ??
        end-point: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：https://abc.oss-cn-shanghai.aliyuncs.com/
        base-path: hy/ # 基础路径
    qiniu-kodo: # 七牛云 kodo ，不使用的情况下可以不写
      - platform: qiniu-kodo-1 # 存储平台标识
        enable-storage: false  # 启用存储
        access-key: ??
        secret-key: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：http://abc.hn-bkt.clouddn.com/
        base-path: base/ # 基础路径
    tencent-cos: # 腾讯云 COS
      - platform: tencent-cos-1 # 存储平台标识
        enable-storage: true  # 启用存储
        secret-id: ??
        secret-key: ??
        region: ?? #存仓库所在地域
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：https://abc.cos.ap-nanjing.myqcloud.com/
        base-path: hy/ # 基础路径
    baidu-bos: # 百度云 BOS
      - platform: baidu-bos-1 # 存储平台标识
        enable-storage: true  # 启用存储
        access-key: ??
        secret-key: ??
        end-point: ?? # 例如 abc.fsh.bcebos.com
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：https://abc.fsh.bcebos.com/abc/
        base-path: hy/ # 基础路径
    upyun-uss: # 又拍云 USS
      - platform: upyun-uss-1 # 存储平台标识
        enable-storage: true  # 启用存储
        username: ??
        password: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：http://abc.test.upcdn.net/
        base-path: hy/ # 基础路径
    minio: # MinIO，由于 MinIO SDK 支持 AWS S3，其它兼容 AWS S3 协议的存储平台也都可配置在这里
      - platform: minio-1 # 存储平台标识
        enable-storage: true  # 启用存储
        access-key: ??
        secret-key: ??
        end-point: ??
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：http://minio.abc.com/abc/
        base-path: hy/ # 基础路径
    aws-s3: # AWS S3，其它兼容 AWS S3 协议的存储平台也都可配置在这里
      - platform: aws-s3-1 # 存储平台标识
        enable-storage: true  # 启用存储
        access-key: ??
        secret-key: ??
        region: ?? # 与 end-point 参数至少填一个
        end-point: ?? # 与 region 参数至少填一个
        bucket-name: ??
        domain: ?? # 访问域名，注意“/”结尾，例如：https://abc.hn-bkt.clouddn.com/
        base-path: s3/ # 基础路径
    ftp: # FTP
      - platform: ftp-1 # 存储平台标识
        enable-storage: true  # 启用存储
        host: ?? # 主机，例如：192.168.1.105
        port: 21 # 端口，默认21
        user: anonymous # 用户名，默认 anonymous（匿名）
        password: "" # 密码，默认空
        domain: ?? # 访问域名，注意“/”结尾，例如：ftp://192.168.1.105/
        base-path: ftp/ # 基础路径
        storage-path: /www/wwwroot/file.abc.com/ # 存储路径，可以配合 Nginx 实现访问，注意“/”结尾，默认“/”
    sftp: # SFTP
      - platform: sftp-1 # 存储平台标识
        enable-storage: true  # 启用存储
        host: ?? # 主机，例如：192.168.1.105
        port: 22 # 端口，默认22
        user: root # 用户名
        password: ?? # 密码或私钥密码
        private-key-path: ?? # 私钥路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等，例如：classpath:id_rsa_2048
        domain: ?? # 访问域名，注意“/”结尾，例如：https://file.abc.com/
        base-path: sftp/ # 基础路径
        storage-path: /www/wwwroot/file.abc.com/ # 存储路径，可以配合 Nginx 实现访问，注意“/”结尾，默认“/”
    webdav: # WebDAV
      - platform: webdav-1 # 存储平台标识
        enable-storage: true  # 启用存储
        server: ?? # 服务器地址，例如：http://192.168.1.105:8405/
        user: ?? # 用户名
        password: ?? # 密码
        domain: ?? # 访问域名，注意“/”结尾，例如：https://file.abc.com/
        base-path: webdav/ # 基础路径
        storage-path: / # 存储路径，可以配合 Nginx 实现访问，注意“/”结尾，默认“/”
    google-cloud: # 谷歌云存储
      - platform: google-1 # 存储平台标识
        enable-storage: true  # 启用存储
        project-id: ?? # 项目 id
        bucket-name: ??
        credentials-path: file:/deploy/example-key.json # 授权 key json 路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等
        domain: ?? # 访问域名，注意“/”结尾，例如：https://storage.googleapis.com/test-bucket/
        base-path: hy/ # 基础路径
```

注意配置每个平台前面都有个`-`号，通过以下方式可以配置多个

```yaml
local:
  - platform: local-1 # 存储平台标识
    enable-storage: true
    enable-access: true
    domain: ""
    base-path: D:/Temp/test/
    path-patterns: /test/file/**
  - platform: local-2 # 存储平台标识，注意这里不能重复
    enable-storage: true
    enable-access: true
    domain: ""
    base-path: D:/Temp/test2/
    path-patterns: /test2/file/**
```

# 编码

在启动类上加上`@EnableFileStorage`注解

```java
@EnableFileStorage
@SpringBootApplication
public class SpringFileStorageTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringFileStorageTestApplication.class, args);
    }
    
}
```

# 开始使用

```java
@RestController
public class FileDetailController {

    @Autowired
    private FileStorageService fileStorageService;//注入实列

    /**
     * 上传文件，成功返回文件 url
     */
    @PostMapping("/upload")
    public String upload(MultipartFile file) {
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath("upload/") //保存到相对路径下，为了方便管理，不需要可以不写
                .setObjectId("0")   //关联对象id，为了方便管理，不需要可以不写
                .setObjectType("0") //关联对象类型，为了方便管理，不需要可以不写
                .putAttr("role","admin") //保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                .upload();  //将文件上传到对应地方
        return fileInfo == null ? "上传失败！" : fileInfo.getUrl();
    }

    /**
     * 上传图片，成功返回文件信息
     * 图片处理使用的是 https://github.com/coobird/thumbnailator
     */
    @PostMapping("/upload-image")
    public FileInfo uploadImage(MultipartFile file) {
        return fileStorageService.of(file)
                .image(img -> img.size(1000,1000))  //将图片大小调整到 1000*1000
                .thumbnail(th -> th.size(200,200))  //再生成一张 200*200 的缩略图
                .upload();
    }

    /**
     * 上传文件到指定存储平台，成功返回文件信息
     */
    @PostMapping("/upload-platform")
    public FileInfo uploadPlatform(MultipartFile file) {
        return fileStorageService.of(file)
                .setPlatform("aliyun-oss-1")    //使用指定的存储平台
                .upload();
    }
}
```