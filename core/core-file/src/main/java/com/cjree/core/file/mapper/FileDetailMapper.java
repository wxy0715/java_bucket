package com.cjree.core.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjree.core.file.entity.FileDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.x.file.storage.core.FileInfo;

import java.util.List;

@Mapper
public interface FileDetailMapper extends BaseMapper<FileDetail> {
    /**
     * 根据前端自定义md5查询文件
     */
    FileInfo selectByUserMetaMd5(String md5);

    /**
     * 清空objectId
     */
    void removeObjectId(String objectId);

    /**
     * 添加objectId
     */
    void addObjectIdByIdList(@Param(value ="idList")List<String> idList, @Param(value ="objectId")String objectId);

}
