package com.cjree.jdk17Template.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjree.core.basic.base.AbstractService;
import com.cjree.core.common.utils.StreamUtils;
import com.cjree.core.model.common.IdCmd;
import com.cjree.core.model.common.Pagination;
import com.cjree.jdk17Template.dto.cmd.CreateExampleCmd;
import com.cjree.jdk17Template.dto.out.ExampleOut;
import com.cjree.jdk17Template.dto.qry.ExampleQry;
import com.cjree.jdk17Template.mapper.api.ExampleMapper;
import com.cjree.jdk17Template.po.ExamplePo;
import com.cjree.jdk17Template.service.api.ExampleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ExampleServiceImpl extends AbstractService<ExamplePo, ExampleMapper> implements ExampleService {

    @Override
    @GlobalTransactional
    public Long addExample(CreateExampleCmd cmd) {
        ExamplePo po = cmdToPo(cmd);
        return super.insert(po).getId();
    }

    @Override
    @GlobalTransactional
    public Long modifyExample(CreateExampleCmd cmd) {
        ExamplePo po = cmdToPo(cmd);
        return super.update(po).getId();
    }

    @Override
    @GlobalTransactional
    public void removeExample(IdCmd idCmd) {
        super.logicDeleteBatch(idCmd.getIdList());
    }

    @Override
    public ExampleOut getExampleById(IdCmd idCmd) {
        ExamplePo po = get(idCmd.getId());
        if (po != null) {
            return poToOut(po);
        }
        return null;
    }

    @Override
    public Pagination<ExampleOut> getExampleListPage(ExampleQry qry) {
        Pagination<ExampleOut> pagination = new Pagination<>(qry.getPageIndex(), qry.getPageSize());
        Page<ExamplePo> page = new Page<>(qry.getPageIndex(), qry.getPageSize());
        QueryWrapper<ExamplePo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(ExamplePo::getCode, qry.getCode())
                .like(ExamplePo::getName, qry.getName());
        page = super.mapper.selectPage(page, queryWrapper);
        if (null == page || page.getRecords().isEmpty()){
            pagination.setTotal(0);
            pagination.setRecords(Collections.emptyList());
            return pagination;
        }
        List<ExampleOut> outList = StreamUtils.convertList(page.getRecords(), this::poToOut);
        pagination.setTotal((int) page.getTotal());
        pagination.setRecords(outList);
        return pagination;
    }

    @Override
    public List<ExampleOut> getExampleList(ExampleQry qry) {
        QueryWrapper<ExamplePo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(ExamplePo::getCode, qry.getCode())
                .like(ExamplePo::getName, qry.getName());
        List<ExamplePo> list = super.listNoSql(queryWrapper);
        if (null ==  list){
            return null;
        }
        return StreamUtils.convertList(list, this::poToOut);
    }

    // 测试异步线程
    @Override
    @Async("customAsyncExecutor")
    public void asyncMethod() {
        // 异步方法中的日志会自动携带traceId
        log.info("这是一个异步方法");
    }
    public static ExamplePo cmdToPo(CreateExampleCmd cmd){
        ExamplePo po =  new ExamplePo(
                cmd.getCode(),
                cmd.getIcCode(),
                cmd.getName(),
                cmd.getPersonName()
        );
        po.setId(cmd.getId());
        return po;
    }

    public ExampleOut poToOut(ExamplePo po){
        return new ExampleOut(
                po.getId(),
                po.getCode(),
                po.getName()
        );
    }
}
