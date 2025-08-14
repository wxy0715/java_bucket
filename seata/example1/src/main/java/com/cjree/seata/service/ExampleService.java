package com.cjree.seata.service;

import com.cjree.core.basic.base.BaseService;
import com.cjree.seata.entity.ExamplePo;

public interface ExampleService extends BaseService<ExamplePo> {


    void save();

}
