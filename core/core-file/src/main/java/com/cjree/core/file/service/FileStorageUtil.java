package com.cjree.core.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cjree.core.common.utils.CoreObjectUtil;
import com.cjree.core.common.utils.ExceptionUtil;
import com.cjree.core.common.utils.StreamUtils;
import com.cjree.core.common.utils.date.DateTool;
import com.cjree.core.common.utils.date.enums.DateStyle;
import com.cjree.core.file.entity.FileDetail;
import com.cjree.core.file.entity.cmd.*;
import com.cjree.core.file.enums.FileType;
import com.cjree.core.file.service.db.FileDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.constant.Constant;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.dromara.x.file.storage.core.upload.FilePartInfoList;
import org.dromara.x.file.storage.core.upload.MultipartUploadSupportInfo;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;


/**
 * 文件存储
 * @author wangxingyu
 */
@Component
@Slf4j
public class FileStorageUtil implements IFileStorage{
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private FileStorageService fileStorageService;
    @Resource
    private FileDetailService fileDetailService;

    /**
     * 生成上传文件信息
     */
    private FileInfo getFileInfo(FileStorageEntity fileStorageEntity, UploadPretreatment of) {
        UploadPretreatment uploadPretreatment = of
                .setHashCalculatorMd5() //计算 MD5
                .setPath(fileStorageEntity.getPath())
                .setObjectId(fileStorageEntity.getObjectId())
                .setObjectType(fileStorageEntity.getObjectType());
        // 文件名设置
        if (CoreObjectUtil.isNotEmpty(fileStorageEntity.getOriginalFilename())) {
            uploadPretreatment.setOriginalFilename(fileStorageEntity.getOriginalFilename());
        } else {
            uploadPretreatment.setOriginalFilename(of.getOriginalFilename());
        }
        // 获取文件类型
        FileType fileType = FileType.typeFromFileName(uploadPretreatment.getOriginalFilename());
        String originalFilename = DateTool.dateToString(DateTool.getCurrentDate(), DateStyle.yyyyMMddHHmmssSSS.getValue()) +"_"+uploadPretreatment.getOriginalFilename();
        uploadPretreatment.setSaveFilename(originalFilename);
        if (FileType.PICTURE.equals(fileType) && fileStorageEntity.getThumbnailEnable()) {
            // 缩略图设置
            uploadPretreatment.setSaveThFilename(uploadPretreatment.getSaveFilename());
            uploadPretreatment.setThumbnailSuffix(".jpg");
            uploadPretreatment.thumbnail(th -> th.size(200,200));
        }
        // 上传文件
        FileInfo fileInfo = uploadPretreatment
                .upload();
        ExceptionUtil.isNull(fileInfo,"上传失败");
        if (fileStorageEntity.getThumbnailEnable()) {
            try {
                String base64Encoded = "";
                if (FileType.PICTURE.equals(fileType)) {
                    // 下载缩略图
                    byte[] bytes = fileStorageService.downloadTh(fileInfo.getUrl()).bytes();
                    base64Encoded = Base64.getEncoder().encodeToString(bytes);
                } else {
                    // 生成缩略图
                    base64Encoded = getFilePreviewImg(fileInfo, fileType);
                }
                fileInfo.getAttr().put("thPresignedByte", base64Encoded);
                FileDetail fileDetail = fileDetailService.toFileDetail(fileInfo);
                fileDetail.setAttr(valueToJson(fileInfo.getAttr()));
                fileDetailService.getBaseMapper().updateById(fileDetail);
            } catch (Exception e) {
                log.error("更新attr失败:{}",e.getMessage());
            }
        }
        return fileInfo;
    }


    public String getFilePreviewImg(FileInfo fileInfo, FileType fileTypeEnum) {
        try {
            // 下载文件内容
            String base64Encoded = "";
            log.info("文件名称：{}", fileInfo.getOriginalFilename());
            switch (fileTypeEnum) {
                case COMPRESS:
                    // 压缩包 "rar", "zip", "jar", "7-zip", "tar", "gzip", "7z"
                    base64Encoded = FileType.COMPRESS.getImageCode();
                    break;
                case DMG:
                    base64Encoded = FileType.DMG.getImageCode();
                    break;
                default:
                    // 其他文件类型不支持
                    base64Encoded = FileType.OTHER.getImageCode();
                    break;
            }
            return base64Encoded;
        } catch (Exception e) {
            log.error("文件类型转换异常:{}",e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo upload(MultipartFile file, FileStorageEntity fileStorageEntity) {
        return getFileInfo(fileStorageEntity, fileStorageService.of(file));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo upload(byte[] file, FileStorageEntity fileStorageEntity) {
        return getFileInfo(fileStorageEntity, fileStorageService.of(file));
    }

    @Override
    public  byte[] fileBytes(String url){
        // 根据url查询origin_name
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        ExceptionUtil.isNull(fileInfo,"该文件已被删除!");
        return fileStorageService.download(url).bytes();
    }

    @Override
    public void download(String url, HttpServletResponse response) throws IOException {
        // 根据url查询origin_name
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        ExceptionUtil.isNull(fileInfo,"该文件已被删除!");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode(fileInfo.getOriginalFilename(), "UTF-8").replaceAll("\\+", "%20");
        response.addHeader("content-disposition","attachment;filename="+fileName);
        response.addHeader("Content-Type", fileInfo.getContentType());
        fileStorageService.download(url)
                .setHashCalculatorMd5() //计算 MD5
                .outputStream(response.getOutputStream());
    }

    @Override
    public void downloadTh(String url, HttpServletResponse response) throws IOException {
        fileStorageService.downloadTh(url)
                .setHashCalculatorMd5() //计算 MD5
                .setHashCalculatorSha256() //计算 SHA256
                .setHashCalculator(Constant.Hash.MessageDigest.SHA512) //指定哈希名称
                .outputStream(response.getOutputStream());
    }

    @Override
    public boolean exists(String url) {
        try {
            return fileStorageService.exists(url);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean delete(String url) {
        if (exists(url)) {
            return fileStorageService.delete(url);
        }
        return true;
    }

    /**
     * 同步附件接口
     * @param fileDetailList 需要同步的附件信息
     * @param objectId 新的业务主键id
     */
    @Override
    public Boolean syncFile(List<FileDetail> fileDetailList,String objectId) {
        for (FileDetail fileDetail : fileDetailList) {
            fileDetail.setId(null);
            fileDetail.setObjectId(objectId);
            fileDetailService.getBaseMapper().insert(fileDetail);
        }
        return Boolean.TRUE;
    }

    /**
     * 查询附件接口
     * @param objectId 关联对象id
     */
    @Override
    public List<FileDetail> queryFileDetailByObjectId(Long objectId) {
        QueryWrapper<FileDetail> fileDetailPoQueryWrapper = new QueryWrapper<>();
        fileDetailPoQueryWrapper.eq(FileDetail.COL_OBJECT_ID,objectId);
        return fileDetailService.getBaseMapper().selectList(fileDetailPoQueryWrapper);
    }

    /**
     * 更新附件接口
     * @param updateEntity 更新对象
     *                     objectId: 关联对象id
     *                     idList: 文件id集合
     */
    @Override
    public boolean updateBatchByObjectId(FileUpdateEntity updateEntity) {
        ExceptionUtil.isNull(updateEntity.getObjectId(),"objectId不能为空");
        // 清空objectId
        fileDetailService.getBaseMapper().removeObjectId(updateEntity.getObjectId());
        // 添加objectId
        if (CoreObjectUtil.isNotEmpty(updateEntity.getIdList())) {
            fileDetailService.getBaseMapper().addObjectIdByIdList(updateEntity.getIdList(), updateEntity.getObjectId());
        }
        return true;
    }


    /**
     * 通过条件查询文件集合
     * @param queryFileEntity 查询条件
     *                        objectIdList: 关联对象id集合
     *                        objectType: 关联对象类型
     */
    @Override
    public List<FileDetail> queryFileList(QueryFileEntity queryFileEntity){
        LambdaQueryWrapper<FileDetail> fileDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileDetailLambdaQueryWrapper.in(CoreObjectUtil.isNotEmpty(queryFileEntity.getObjectIdList()),FileDetail::getObjectId,queryFileEntity.getObjectIdList());
        fileDetailLambdaQueryWrapper.eq(CoreObjectUtil.isNotEmpty(queryFileEntity.getObjectType()),FileDetail::getObjectType, queryFileEntity.getObjectType());
        return fileDetailService.list(fileDetailLambdaQueryWrapper);
    }



    /**
     * 初始化任务
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileInfo initPart(FileTaskPartEntity fileTaskPartEntity) {
        ExceptionUtil.isNull(fileTaskPartEntity.getLength(),"文件大小不能为空");
        ExceptionUtil.isNull(fileTaskPartEntity.getOriginalFilename(),"文件名不能为空");
        // 判断存储平台是否支持
        FileStorage fileStorage;
        if (CoreObjectUtil.isNotEmpty(fileTaskPartEntity.getPlatform())) {
            fileStorage = fileStorageService.getFileStorage(fileTaskPartEntity.getPlatform());
        } else {
            fileStorage = fileStorageService.getFileStorage();
        }
        MultipartUploadSupportInfo supportInfo = fileStorageService.isSupportMultipartUpload(fileStorage);
        ExceptionUtil.isTrue(!supportInfo.getIsSupport(), "当前存储平台【"+fileStorage.getPlatform()+"】不支持此功能");
        // 根据md5查询匹配的文件信息
        FileInfo fileInfo = fileDetailService.getBaseMapper().selectByUserMetaMd5(fileTaskPartEntity.getMd5());
        if (fileInfo != null) {
            return fileInfo;
        }
        // 初始化一个任务
       fileInfo = fileStorageService
                .initiateMultipartUpload()
                .setPath(fileTaskPartEntity.getPath())
                .setObjectId(fileTaskPartEntity.getObjectId())
                .setObjectType(fileTaskPartEntity.getObjectType())
                .setOriginalFilename(fileTaskPartEntity.getOriginalFilename())
                .setSaveFilename(DateTool.dateToString(DateTool.getCurrentDate(), DateStyle.yyyyMMddHHmmssSSS.getValue()) +"_"+fileTaskPartEntity.getOriginalFilename())
                .setSize(fileTaskPartEntity.getLength())
                .putUserMetadata("md5", fileTaskPartEntity.getMd5())
                .init();
        log.info("手动分片上传文件初始化成功：{}", fileInfo);
        return fileInfo;
    }

    /**
     * 上传分片
     */
    @Override
    public Integer uploadPart(FilePartUploadEntity filePartUploadEntity) {
        ExceptionUtil.isNull(filePartUploadEntity.getUrl(),"url不能为空");
        ExceptionUtil.isNull(filePartUploadEntity.getIndex(),"索引不能为空");
        MultipartFile file = filePartUploadEntity.getFile();
        // 验证分片是否上传完成
        Integer index = checkPart(filePartUploadEntity.getUrl(), filePartUploadEntity.getIndex());
        if (index != -1) {
            log.info("分片已存在");
            return filePartUploadEntity.getIndex();
        }
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(filePartUploadEntity.getUrl());
        if (CoreObjectUtil.isEmpty(fileInfo.getContentType())) {
            String contentType = file.getContentType();
            fileInfo.setContentType(contentType);
            fileStorageService.getFileRecorder().update(fileInfo);
        }
        FilePartInfo filePartInfo = null;
        try {
            filePartInfo = fileStorageService
                    .uploadPart(fileInfo, filePartUploadEntity.getIndex(), file.getInputStream())
                    .setHashCalculatorMd5()
                    .setHashCalculatorSha256()
                    .upload();
        } catch (IOException e) {
            log.error("上传失败",e);
            ExceptionUtil.error("上传失败");
        }
        log.info("分片上传成功：{}", filePartInfo);
        return filePartUploadEntity.getIndex();
    }

    /**
     * 校验文件分片是否上传完成
     */
    @Override
    public Integer checkPart(String url, Integer index) {
        // 查询主表信息
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        ExceptionUtil.isNull(fileInfo,"该任务不存在,请重新上传!");
        // 判断是否主文件已经上传完成
        if (fileInfo.getUploadStatus() == 2) {
            return index;
        }
        // 获取上传完成的分片信息
        FilePartInfoList partList = fileStorageService.listParts(fileInfo).listParts();
        FilePartInfo first = StreamUtils.findFirst(partList.getList(), info -> info.getPartNumber().equals(index));
        if (first != null) {
            return index;
        }
        return -1;
    }

    /**
     * 合并分片
     */
    @Override
    public Boolean mergePart(String url) {
        // 查询主表信息
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        ExceptionUtil.isNull(fileInfo,"该任务不存在,请重新上传!");
        // 判断是否主文件已经上传完成
        if (fileInfo.getUploadStatus() == 2) {
            return true;
        }
        fileStorageService
                .completeMultipartUpload(fileInfo)
                .complete();
        log.info("合并文件成功：{}", fileInfo);
        return true;
    }

    /**
     * 取消分片
     */
    @Override
    public Boolean cancelPart(String url) {
        // 查询主表信息
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(url);
        ExceptionUtil.isNull(fileInfo,"该任务不存在,请重新上传!");
        // 判断是否主文件已经上传完成
        if (fileInfo.getUploadStatus() == 2) {
            ExceptionUtil.error("文件已合并成功,不能取消");
        }
        FilePartInfoList partList = fileStorageService.listParts(fileInfo).listParts();
        for (FilePartInfo info : partList.getList()) {
            log.info("列举已上传的分片：{}", info);
        }
        fileStorageService.abortMultipartUpload(fileInfo).abort();
        try {
            partList = null;
            partList = fileStorageService.listParts(fileInfo).listParts();
        } catch (Exception e) {
        }
        ExceptionUtil.isTrue(CoreObjectUtil.isNotArray(partList), "手动分片上传文件取消失败！");
        log.info("手动分片上传文件取消成功：{}", fileInfo);
        return true;
    }


    /**
     * 将指定值转换成 json 字符串
     */
    public String valueToJson(Object value) throws JsonProcessingException {
        if (value == null) return null;
        return objectMapper.writeValueAsString(value);
    }
}
