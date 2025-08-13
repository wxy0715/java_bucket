package com.cjree.core.file.service.db;

import org.dromara.x.file.storage.core.upload.FilePartInfo;

/**
 * 解决动态代理问题
 */
public interface IFilePartDetailService {
    void saveFilePart(FilePartInfo info);
    void deleteFilePartByUploadId(String uploadId);
}
