package com.wxy.springfilestorage.controller;


import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.wxy.springfilestorage.entity.FileDetail;
import com.wxy.springfilestorage.entity.FileStorageEntity;
import com.wxy.springfilestorage.service.impl.FileDetailService;
import com.wxy.springfilestorage.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 文件上传控制器
 * @author wangxingyu
 * @date 2023/01/28
 * todo
 * 1.目录形式展示
 * 2.精确查询
 * 3.新建目录
 * 4.批量删除
 */
@RestController
public class FileController {

    @Autowired
    private FileStorageUtil fileStorageUtil;
    @Autowired
    private FileDetailService fileDetailService;
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public FileInfo upload(@RequestParam("file") MultipartFile file, FileStorageEntity fileStorageEntity) {
        return fileStorageUtil.upload(file, fileStorageEntity);
    }

    /**
     * 下载文件
     */
    @PostMapping("/download")
    public void download(HttpServletResponse response,String url) throws IOException {
        fileStorageUtil.download(url, response);
    }

    /**
     * 根据文件所属对象id查询文件信息
     */
    @PostMapping("/queryFileDetailByObjectId")
    public List<FileDetail> queryFileDetailByObjectId(@RequestParam("id") Long id){
        return fileDetailService.queryFileDetailByObjectId(id);
    }

    /**
     * 更新附件
     */
    @PostMapping("/updateBatchById")
    public Boolean updateBatchById(@RequestBody List<FileDetail> list){
        return fileDetailService.updateBatchById(list);
    }

    /**
     * 删除附件
     */
    @PostMapping("/removeBatchByIds")
    public Boolean removeBatchByIds(@RequestBody List<Long> idList){
        return fileDetailService.removeBatchByIds(idList);
    }
}
