package com.wxy.transactional.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.transactional.config.TransactionUtils;
import com.wxy.transactional.entity.Test;
import com.wxy.transactional.entity.User;
import com.wxy.transactional.enums.AgeEnum;
import com.wxy.transactional.enums.SexEnum;
import com.wxy.transactional.mapper.TestMapper;
import com.wxy.transactional.mapper.UserMapper;
import com.wxy.transactional.service.ListUtils;
import com.wxy.transactional.service.api.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * https://mp.weixin.qq.com/s?__biz=MzAxMjY5NDU2Ng==&mid=2651861885&idx=1&sn=e4ef0edead70912e9361b541e3a9810d&chksm=80497634b73eff22c4fed2f915184ef39627906727b8a61631cdf9142fc4dfbd354e313ae8a1&scene=27
 * @author wxy
 * @since 2023-01-06
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    TransactionUtils transactionUtils;
    @Autowired
    TestMapper testMapper;
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public User get() {
        // 此方法是存在同一个事务中,并且走了代理则调用方法的事务注解将会生效
        IUserService userService = (IUserService)AopContext.currentProxy();
        userService.get1();
        userService.get2();
        return null;
    }

    @Override
    @Transactional
    public void get3(){
        // 此方法是存在同一个事务中,但是各自方法的事务注解将会失效
        this.get1();
        this.get2();
    }

    @Override
    public void get4(){
        TransactionStatus transactionStatus = transactionUtils.begin(TransactionDefinition.ISOLATION_READ_COMMITTED, TransactionDefinition.PROPAGATION_REQUIRED);
        try {
            this.get1();  // 回滚

            TransactionStatus transactionStatus1 = transactionUtils.begin(TransactionDefinition.ISOLATION_READ_COMMITTED, TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            try {
                this.get2(); // 不回滚
                transactionUtils.commit(transactionStatus1);
            } catch (Exception e) {
                transactionUtils.rollback(transactionStatus1);
            }

            this.get21(); // 回滚
            transactionUtils.commit(transactionStatus);
        } catch (Exception e) {
            transactionUtils.rollback(transactionStatus);
        }
    }

    @Override
    //@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void get1(){
        User user = new User();
        user.setSex(SexEnum.MAN);
        user.setAge(AgeEnum.ONE);
        user.setName("1");
        user.setEmail("1");
        baseMapper.insert(user);
    }


    @Override
    public void get2(){
        User user = new User();
        user.setSex(SexEnum.MAN);
        user.setAge(AgeEnum.ONE);
        user.setName("2");
        user.setEmail("2");
        baseMapper.insert(user);
/*        int i = 1/0;
        User user1 = new User();
        user1.setSex(SexEnum.MAN);
        user1.setAge(AgeEnum.ONE);
        user1.setName("3");
        user1.setEmail("3");
        baseMapper.insert(user1);*/
    }

    @Override
    public void get21(){
        User user = new User();
        user.setSex(SexEnum.MAN);
        user.setAge(AgeEnum.ONE);
        user.setName("21");
        user.setEmail("21");
        baseMapper.insert(user);
        int i = 1/0;
        User user1 = new User();
        user1.setSex(SexEnum.MAN);
        user1.setAge(AgeEnum.ONE);
        user1.setName("31");
        user1.setEmail("31");
        baseMapper.insert(user1);
    }

    private final ArrayBlockingQueue queue = new ArrayBlockingQueue(8,true);

    private final ThreadPoolExecutor.CallerRunsPolicy policy = new ThreadPoolExecutor.CallerRunsPolicy();

    //1、创建核心线程为10的线程池
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10,15,10, TimeUnit.SECONDS,queue,policy);
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    @Transactional
    public void test() throws SQLException {
        // 手动开启线程
        //2、根据sqlSessionTemplate获取SqlSession工厂
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //3、获取Connection来手动控制事务
        Connection connection = sqlSession.getConnection();
        try {
            connection.setAutoCommit(false);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            // 创建测试插入数据
            List<User> list = new ArrayList<>(10000);
            for(int i = 1; i <= 10000; i++) {
                User user = new User();
                user.setId((long) i);
                user.setName(i+"");
                list.add(user);
            }

            // 主线程插入数据
            TestMapper mapper1 = sqlSession.getMapper(TestMapper.class);
            Test test = new Test();
            test.setName("1");
            mapper1.insert(test);
            // 拆分数据
            List<List<User>> lists = ListUtils.averageAssign(list,1000);
            List<Callable<Integer>> callableList = new ArrayList<>();
            // 新建任务列表
            for (List<User> users : lists) {
                Callable<Integer> objectCallable = new Callable<Integer>(){
                    @Override
                    public Integer call() throws Exception {
                        Integer n = 0;
                        try {
                            n = mapper.saveBatch(users);
                            int i = 3/0;
                        } catch (Exception e) {
                            log.error(e.toString(),e);
                        }
                        return n;
                    }
                };
                callableList.add(objectCallable);
            }
            List<Future<Integer>> futureList = executor.invokeAll(callableList);
            for (Future<Integer> booleanFuture : futureList) {
                if (booleanFuture.get()<=0) {
                    connection.rollback();
                    return;
                }
            }
            connection.commit();
        } catch (Exception e) {
            log.error(e.toString());
            connection.rollback();
        }
    }
}