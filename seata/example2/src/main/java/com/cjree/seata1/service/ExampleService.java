package com.cjree.seata1.service;

import com.cjree.core.basic.base.BaseService;
import com.cjree.seata1.entity.ExamplePo;

public interface ExampleService extends BaseService<ExamplePo> {


    void save();

}
