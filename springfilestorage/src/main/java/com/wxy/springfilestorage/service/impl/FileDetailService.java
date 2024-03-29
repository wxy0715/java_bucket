package com.wxy.springfilestorage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.recorder.FileRecorder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.springfilestorage.entity.FileDetail;
import com.wxy.springfilestorage.mapper.FileDetailMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 文件记录表 服务实现类
 * @author wxy
 * @since 2023-01-28
 */
@Service
public class FileDetailService extends ServiceImpl<FileDetailMapper, FileDetail> implements FileRecorder {
    @Resource
    private FileDetailMapper fileDetailMapper;
    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean record(FileInfo info) {
        FileDetail detail = BeanUtil.copyProperties(info,FileDetail.class,"attr");
        return save(detail);
    }

    /**
     * 根据 url 查询文件信息
     */
    @SneakyThrows
    @Override
    public FileInfo getByUrl(String url) {
        FileDetail detail = getOne(new QueryWrapper<FileDetail>().eq("url",url));
        return BeanUtil.copyProperties(detail,FileInfo.class,"attr");
    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        return remove(new QueryWrapper<FileDetail>().eq("url",url));
    }

    public List<FileDetail> queryFileDetailByObjectId(Long id) {
        QueryWrapper<FileDetail> fileDetailPoQueryWrapper = new QueryWrapper<>();
        fileDetailPoQueryWrapper.eq("object_id",id);
        return baseMapper.selectList(fileDetailPoQueryWrapper);
    }
}
