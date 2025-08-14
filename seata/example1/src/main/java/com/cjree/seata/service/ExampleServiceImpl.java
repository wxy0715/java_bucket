package com.cjree.seata.service;

import com.cjree.core.basic.base.AbstractService;
import com.cjree.seata.controller.ExampleController;
import com.cjree.seata.entity.ExamplePo;
import com.cjree.seata.mapper.api.ExampleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *@Transactional(propagation=Propagation.REQUIRED) //如果有事务, 那么加入事务, 没有的话新建一个(默认情况下)
 * @Transactional(propagation=Propagation.NOT_SUPPORTED) //容器不为这个方法开启事务
 * @Transactional(propagation=Propagation.REQUIRES_NEW) //不管是否存在事务,都创建一个新的事务,原来的挂起,新的执行完毕,继续执行老的事务
 * @Transactional(propagation=Propagation.MANDATORY) //必须在一个已有的事务中执行,否则抛出异常
 * @Transactional(propagation=Propagation.NEVER) //必须在一个没有的事务中执行,否则抛出异常(与Propagation.MANDATORY相反)
 * @Transactional(propagation=Propagation.SUPPORTS) //如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.
 * @Transactional(propagation=Propagation. NESTED)//支持当前事务，如果当前事务存在，则执行一个嵌套事务，如果当前没有事务，就新建一个事务。
 *
 * @Transactional(isolation = Isolation.READ_UNCOMMITTED)//读取未提交数据(会出现脏读, 不可重复读) 基本不使用
 * @Transactional(isolation = Isolation.READ_COMMITTED)//读取已提交数据(会出现不可重复读和幻读)
 * @Transactional(isolation = Isolation.REPEATABLE_READ)//可重复读(会出现幻读)
 * @Transactional(isolation = Isolation.SERIALIZABLE)//串行化
 * @author wxy
 * @since 2023-01-06
 */
@Service
@Slf4j
public class ExampleServiceImpl extends AbstractService<ExamplePo, ExampleMapper> implements ExampleService {

    @Autowired
    private ExampleController exampleController;

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void save(){
        ExamplePo examplePo = new ExamplePo();
        examplePo.setName("1");
        examplePo.setCode("1");
        examplePo.setPersonName("1");
        examplePo.setIcCode("1");
        mapper.insert(examplePo);
        log.info("保存1成功,调用2开始...");
        exampleController.save();
    }

}
