package com.wxy.mongo.dao;

import com.wxy.mongo.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDao extends MongoRepository<Account,Long> {
    List<Account> findByProjectId(Long projectId);

//    @Query("{'_id': ?0}")
//    void upsertAccountByAccountId(Long accountId, Account account);
}
