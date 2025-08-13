package com.cjree.core.file.service.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cjree.core.file.entity.FilePartDetail;
import com.cjree.core.file.mapper.FilePartDetailMapper;
import lombok.SneakyThrows;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Service;

/**
 * 用来将文件分片上传记录保存到数据库，这里使用了 MyBatis-Plus 和 Hutool 工具类
 * 目前仅手动分片分片上传时使用
 */
@Service
public class FilePartDetailService extends ServiceImpl<FilePartDetailMapper, FilePartDetail> implements IFilePartDetailService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存文件分片信息
     * @param info 文件分片信息
     */
    @SneakyThrows
    @Override
    public void saveFilePart(FilePartInfo info) {
        FilePartDetail detail = toFilePartDetail(info);
        if (save(detail)) {
            info.setId(detail.getId());
        }
    }

    /**
     * 删除文件分片信息
     */
    @Override
    public void deleteFilePartByUploadId(String uploadId) {
        remove(new QueryWrapper<FilePartDetail>().eq(FilePartDetail.COL_UPLOAD_ID, uploadId));
    }


    /**
     * 将 FilePartInfo 转成 FilePartDetail
     * @param info 文件分片信息
     */
    public FilePartDetail toFilePartDetail(FilePartInfo info) throws JsonProcessingException {
        FilePartDetail detail = new FilePartDetail();
        detail.setPlatform(info.getPlatform());
        detail.setUploadId(info.getUploadId());
        detail.setETag(info.getETag());
        detail.setPartNumber(info.getPartNumber());
        detail.setPartSize(info.getPartSize());
        detail.setHashInfo(valueToJson(info.getHashInfo()));
        detail.setCreateTime(info.getCreateTime());
        return detail;
    }

    /**
     * 将指定值转换成 json 字符串
     */
    public String valueToJson(Object value) throws JsonProcessingException {
        if (value == null) return null;
        return objectMapper.writeValueAsString(value);
    }
}
