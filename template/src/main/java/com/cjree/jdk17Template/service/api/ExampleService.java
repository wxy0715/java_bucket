package com.cjree.jdk17Template.service.api;


import com.cjree.core.basic.base.BaseService;
import com.cjree.core.model.common.IdCmd;
import com.cjree.core.model.common.Pagination;
import com.cjree.core.model.validate.QueryPage;
import com.cjree.jdk17Template.dto.cmd.CreateExampleCmd;
import com.cjree.jdk17Template.dto.out.ExampleOut;
import com.cjree.jdk17Template.dto.qry.ExampleQry;
import com.cjree.jdk17Template.po.ExamplePo;
import org.springframework.validation.annotation.Validated;

import java.util.List;


public interface ExampleService extends BaseService<ExamplePo> {

    /**
     * 新增
     */
    Long addExample(CreateExampleCmd cmd);

    /**
     * 修改
     */
    Long modifyExample(CreateExampleCmd cmd);

    /**
     * 删除（批量）
     */
    void removeExample(IdCmd idCmd);

    /**
     * 根据 id 获取
     */
    ExampleOut getExampleById(IdCmd idCmd);

    /**
     * 获取分页
     */
    @Validated(value = {QueryPage.class})
    Pagination<ExampleOut> getExampleListPage(ExampleQry qry);

    /**
     * 查询可用授权列表
     */
    List<ExampleOut> getExampleList(ExampleQry qry);


    /**
     * 测试异步线程
     */
    void asyncMethod();

}
