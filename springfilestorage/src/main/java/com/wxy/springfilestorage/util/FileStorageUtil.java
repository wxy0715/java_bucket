package com.wxy.springfilestorage.util;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import com.wxy.springfilestorage.entity.FileStorageEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件存储
 * @author wangxingyu
 * @date 2023/01/28
 */
@Component
public class FileStorageUtil {
    @Resource
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    public FileInfo upload(MultipartFile file, FileStorageEntity fileStorageEntity) {
        return getFileInfo(fileStorageEntity, fileStorageService.of(file));
    }

    /**
     * 上传文件
     */
    public FileInfo upload(byte[] file, FileStorageEntity fileStorageEntity) {
        return getFileInfo(fileStorageEntity, fileStorageService.of(file));
    }

    private FileInfo getFileInfo(FileStorageEntity fileStorageEntity, UploadPretreatment of) {
        UploadPretreatment uploadPretreatment = of
                .setPath("/")
                .setObjectId(fileStorageEntity.getObjectId())
                .setObjectType(fileStorageEntity.getObjectType());
        FileInfo fileInfo = uploadPretreatment.upload();
        if (fileInfo == null) {
            throw new RuntimeException("上传失败");
        }
        return fileInfo;
    }

    /**
     * 下载文件
     * @param url          url
     * @param response 输出流
     */
    public void download(String url, HttpServletResponse response) throws IOException {
        fileStorageService.download(url).outputStream(response.getOutputStream());
    }

    /**
     * 下载缩略图
     * @param url          url
     * @param response 输出流
     */
    public void downloadTh(String url, HttpServletResponse response) throws IOException {
        fileStorageService.downloadTh(url).outputStream(response.getOutputStream());
    }
}
