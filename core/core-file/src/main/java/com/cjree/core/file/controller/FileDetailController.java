package com.cjree.core.file.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.Result;
import com.cjree.core.common.utils.ExceptionUtil;
import com.cjree.core.file.entity.*;
import com.cjree.core.file.entity.cmd.*;
import com.cjree.core.file.entity.out.FilePresignedOut;
import com.cjree.core.file.service.db.FileDetailService;
import com.cjree.core.file.service.FileStorageUtil;
import com.cjree.core.model.common.IdCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.spring.FileStorageAutoConfiguration;
import org.dromara.x.file.storage.spring.SpringFileStorageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cjree.core.basic.common.Response.ok;

@RequestMapping("/XSpring")
@Tag(name = "文件传输")
@RestController
@Import({FileStorageAutoConfiguration.class, SpringFileStorageProperties.class})
@Slf4j
public class FileDetailController {
    @Resource
    private FileStorageUtil fileStorageUtil;
    @Resource
    private FileDetailService fileDetailService;
    @Resource
    private FileStorageService fileStorageService;

    @Value("${kkFileViewUrl:http://192.168.30.49:8012/onlinePreview}")
    private String kkFileViewUrl;

    @PostMapping("/upload")
    @Operation(summary = "上传")
    public Result<FileInfo> upload(@RequestParam("file") MultipartFile file, FileStorageEntity fileStorageEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.upload(file,fileStorageEntity));
    }

    @PostMapping("/uploadList")
    @Operation(summary = "上传批量")
    public Result<FileInfo> uploadList(FileStorageEntity fileStorageEntity){
        ExceptionUtil.isNull(fileStorageEntity.getFileList(),"上传文件不能为空");
        List<FileInfo> fileInfoList = new ArrayList<>();
        for (MultipartFile multipartFile : fileStorageEntity.getFileList()) {
            FileInfo upload = fileStorageUtil.upload(multipartFile, fileStorageEntity);
            fileInfoList.add(upload);
        }
        return ok(ResponseCode.SUCCESS,fileInfoList);
    }

    @PostMapping("/presigned")
    @Operation(summary = "预签名")
    public Result<FilePresignedOut> presigned(@RequestBody FileStorageEntity fileStorageEntity){
        // 获取一个 FileInfo
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(fileStorageEntity.getPath());
        String presignedUrl = fileStorageService.generatePresignedUrl(fileInfo, DateUtil.offsetHour(new Date(), 1));
        FilePresignedOut filePresignedOut = new FilePresignedOut();
        filePresignedOut.setUrl(presignedUrl);
        filePresignedOut.setKkFileViewUrl(kkFileViewUrl);
        try {
            //根据获得的 URL 下载文件
            byte[] downloadBytes = HttpUtil.downloadBytes(presignedUrl);
            filePresignedOut.setArray(downloadBytes);
        } catch (Exception e) {
            log.error("下载文件错误:{}",e.getMessage());
        }
        return ok(ResponseCode.SUCCESS,filePresignedOut);
    }

    @PostMapping("/download")
    @Operation(summary = "下载")
    public Result<Object> download(HttpServletResponse response, @RequestParam("url") String url) throws IOException{
        fileStorageUtil.download(url,response);
        return ok(ResponseCode.SUCCESS);
    }

    @PostMapping("/downloadTh")
    @Operation(summary = "下载缩略图")
    public Result<Object> downloadTh(HttpServletResponse response, @RequestParam("url") String url) throws IOException{
        fileStorageUtil.downloadTh(url,response);
        return ok(ResponseCode.SUCCESS);
    }

    @PostMapping("/delete")
    @Operation(summary = "根据url删除文件")
    public Result<Boolean> delete(@RequestBody FilePartCheckEntity filePartCheckEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.delete(filePartCheckEntity.getUrl()));
    }

    @PostMapping("/queryFileDetailByObjectId")
    @Operation(summary = "根据文件所属对象id查询文件信息")
    public Result<FileDetail> queryFileDetailByObjectId(@RequestParam("id") Long id){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.queryFileDetailByObjectId(id));
    }

    @Operation(summary = "查询文件集合")
    @PostMapping("/queryFileList")
    public Result<FileDetail> queryFileList(@RequestBody QueryFileEntity queryFileEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.queryFileList(queryFileEntity));
    }

    @PostMapping("/existsFile")
    @Operation(summary = "根据url判断文件是否存在")
    public Result<Boolean> queryFileDetailByObjectId(@RequestParam("url") String url){
        boolean exists = fileStorageUtil.exists(url);
        return ok(ResponseCode.SUCCESS,exists);
    }

    @PostMapping("/updateBatchByObjectId")
    @Operation(summary = "通过objectId更新附件")
    public Result<Boolean> updateBatchByObjectId(@RequestBody FileUpdateEntity updateEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.updateBatchByObjectId(updateEntity));
    }
    @PostMapping("/removeBatchByIds")
    @Operation(summary = "根据文件id集合删除附件")
    public Result<Boolean> removeBatchByIds(@RequestBody IdCmd idCmd){
        return ok(ResponseCode.SUCCESS,fileDetailService.removeBatchByIds(idCmd.getIdList()));
    }

    @PostMapping("/initPart")
    @Operation(summary = "初始化分片任务")
    public Result<FileInfo> initPart(@RequestBody FileTaskPartEntity fileTaskPartEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.initPart(fileTaskPartEntity));
    }

    @PostMapping("/checkPart")
    @Operation(summary = "判断分片是否上传完成")
    public Result<Integer> checkPart(@RequestBody FilePartCheckEntity filePartCheckEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.checkPart(filePartCheckEntity.getUrl(),filePartCheckEntity.getIndex()));
    }

    @PostMapping("/uploadPart")
    @Operation(summary = "分片上传")
    public Result<Integer> uploadPart(FilePartUploadEntity filePartUploadEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.uploadPart(filePartUploadEntity));
    }

    @PostMapping("/mergePart")
    @Operation(summary = "合并分片")
    public Result<Boolean> mergePart(@RequestBody FilePartCheckEntity filePartCheckEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.mergePart(filePartCheckEntity.getUrl()));
    }

    @PostMapping("/cancelPart")
    @Operation(summary = "取消分片上传")
    public Result<Boolean> cancelPart(@RequestBody FilePartCheckEntity filePartCheckEntity){
        return ok(ResponseCode.SUCCESS,fileStorageUtil.cancelPart(filePartCheckEntity.getUrl()));
    }
}
