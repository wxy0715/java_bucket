package com.cjree.core.file.service;

import com.cjree.core.file.entity.FileDetail;
import com.cjree.core.file.entity.cmd.*;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFileStorage {
    FileInfo upload(MultipartFile file, FileStorageEntity fileStorageEntity);

    FileInfo upload(byte[] file, FileStorageEntity fileStorageEntity);

    /**
     * 获取文件字节数组
     */
    byte[] fileBytes(String url);

    /**
     * 下载文件
     */
    void download(String url, HttpServletResponse response) throws IOException;

    /**
     * 下载缩略图
     */
    void downloadTh(String url, HttpServletResponse response) throws IOException;

    /**
     * 根据url判断文件是否存在
     */
    boolean exists(String url);

    /**
     * 删除文件
     */
    Boolean delete(String url);

    /**
     * 同步附件接口
     * @param fileDetailList 需要同步的附件信息
     * @param objectId 新的业务主键id
     */
    Boolean syncFile(List<FileDetail> fileDetailList, String objectId);

    /**
     * 查询附件接口
     * @param objectId 关联对象id
     */
    List<FileDetail> queryFileDetailByObjectId(Long objectId);

    /**
     * 更新附件接口
     * @param updateEntity 更新对象
     *                     objectId: 关联对象id
     *                     idList: 文件id集合
     */
    boolean updateBatchByObjectId(FileUpdateEntity updateEntity);

    /**
     * 通过条件查询文件集合
     * @param queryFileEntity 查询条件
     *                        objectIdList: 关联对象id集合
     *                        objectType: 关联对象类型
     */
    List<FileDetail> queryFileList(QueryFileEntity queryFileEntity);


    // -----------------以下是分片逻辑-----------------------------------------------------

    /**
     * 初始化任务
     */
    FileInfo initPart(FileTaskPartEntity fileTaskPartEntity);

    /**
     * 上传分片
     */
    Integer uploadPart(FilePartUploadEntity filePartUploadEntity);

    /**
     * 校验文件分片是否上传完成
     */
    Integer checkPart(String url, Integer index);

    /**
     * 合并分片
     */
    Boolean mergePart(String url);

    /**
     * 取消分片
     */
    Boolean cancelPart(String url);
}
