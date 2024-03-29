package com.wxy.utils.transaction;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;

/**
 * 事务工具类
 * @author wangxingyu
 */
@Component
public class TransactionUtils {
    @Resource
    private DataSourceTransactionManager transactionManager;


    /**
     * 开启事务,传入隔离级别
     * @param isolationLevel        隔离级别
     * @param transactionDefinition 事务定义
     * @return {@link TransactionStatus}
     */
    public TransactionStatus begin(Integer isolationLevel,Integer transactionDefinition) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 事物隔离级别，开启新事务 TransactionDefinition.ISOLATION_READ_COMMITTED
        def.setIsolationLevel(isolationLevel == null ? TransactionDefinition.ISOLATION_READ_COMMITTED : isolationLevel);
        // 事务传播行为
        def.setPropagationBehavior(transactionDefinition==null ? TransactionDefinition.PROPAGATION_REQUIRED : transactionDefinition);
        //默认事务
        return transactionManager.getTransaction(def);
    }

    /**
     * 提交事务
     * @param transaction 事务
     */
    public void commit(TransactionStatus transaction) {
        transactionManager.commit(transaction);
    }


    /**
     * 回滚事务
     * @param transaction 事务
     */
    public void rollback(TransactionStatus transaction) {
        transactionManager.rollback(transaction);
    }
}