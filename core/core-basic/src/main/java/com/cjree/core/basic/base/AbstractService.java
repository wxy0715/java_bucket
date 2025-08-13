package com.cjree.core.basic.base;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.AopUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjree.core.basic.invoker.TransactionalInvoker;
import com.cjree.core.basic.util.BeanRefUtil;
import com.cjree.core.cache.base.Cacheable;
import com.cjree.core.cache.factory.CacheUtil;
import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.config.SpringContainer;
import com.cjree.core.common.exception.DataAccessException;
import com.cjree.core.common.exception.Error;
import com.cjree.core.model.common.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.cjree.core.common.constants.CacheName.REDIS;

/**
 * 基础服务类：封装了一系列基础的增删改查操作和保存缓存操作，并自动注入当前的model和dao
 * @param <T> model 实体
 * @param <M> mapper dao对象
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class AbstractService<T extends BaseModel, M extends BaseMapper<T>> implements BaseService<T> {

    private volatile SqlSessionFactory sqlSessionFactory;
    protected Class<T> entityClass = this.currentModelClass();

    protected Class<T> mapperClass = this.currentMapperClass();

    @Autowired
    protected M mapper;
    @Autowired
    protected RedissonClient redissonClient;
    @Autowired
    protected TransactionalInvoker transactionalInvoker;

    public M getBaseMapper() {
        return this.mapper;
    }

    protected String getEntityName() {
        String applicationName = SpringContainer.getProperty("spring.application.name");
        if (StringUtils.isEmpty(applicationName)) {
            applicationName = "default";
        }
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypes = parameterizedType.getActualTypeArguments();
        Class<T> tClass = (Class<T>) actualTypes[0];
        return applicationName + ":cache:" + tClass.getSimpleName();
    }

    /**
     * 批量获取缓存
     * @param ids 主键集合
     * @return 实体集合
     */
    private List<T> getCacheBatch(List<Long> ids) {
        List<T> records;
        try {
            records = CacheUtil.getCacheCommand().getBatch(ids.stream()
                            .map(i -> Cacheable.getCacheKey(getEntityName(), i))
                            .collect(Collectors.toList()))
                    .stream().map(r -> (T) r)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 使用异步删除缓存
            CompletableFuture.runAsync(() -> {
                CacheUtil.getCacheCommand().delBatch(ids.stream()
                        .map(i -> Cacheable.getCacheKey(getEntityName(), i))
                        .collect(Collectors.toList()));
            });
            records = new ArrayList<>(Collections.nCopies(ids.size(), null));
        }
        return records;
    }

    /**
     * 获取缓存
     * @param id 主键
     * @return 实体
     */
    private T getCache(Long id) {
        String cacheKey = Cacheable.getCacheKey(getEntityName(), id);
        T record = null;
        try {
            // 获取此缓存是否正等待清理中
            String waitDeleteCacheKey = (String) CacheUtil.getCacheCommand().get("TC_WaitDeleteCacheKey:" + cacheKey);
            if (waitDeleteCacheKey == null) {
                try {
                    record = (T) CacheUtil.getCacheCommand().get(cacheKey);
                } catch (Exception e) {
                    // 缓存的数据不能反序列化,删除旧的缓存 (比如上线过程中修改数据库)
                    CacheUtil.getCacheCommand().del(cacheKey);
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
        return record;
    }

    /**
     * 批量插入缓存
     */
    private void setCacheBatch(List<T> records) {
        if (records.isEmpty() || !(records.get(0) instanceof Cacheable)) {
            return;
        }
        try {
            Map<String, Serializable> map = records.stream()
                    .collect(Collectors.toMap(r -> ((Cacheable) r).getCacheKey().toString(), r -> r));
            CacheUtil.getCacheCommand().setBatch(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 插入缓存
     */
    private void setCache(T record) {
        if (!(record instanceof Cacheable)) {
            return;
        }
        Cacheable cacheable = (Cacheable) record;
        try {
            CacheUtil.getCacheCommand().set(cacheable.getCacheKey().toString(), record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 批量删除缓存
     */
    private void deleteCacheBatch(List<T> records) {
        if (records.size() == 0 || !(records.get(0) instanceof Cacheable)) {
            return;
        }
        try {
            CacheUtil.getCacheCommand().delBatch(records.stream()
                    .map(r -> Cacheable.getCacheKey(getEntityName(), r.getId())).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 删除缓存
     */
    private void deleteCache(T record) {
        if (!(record instanceof Cacheable)) {
            return;
        }
        Cacheable cacheable = (Cacheable) record;
        try {
            CacheUtil.getCacheCommand().del(cacheable.getCacheKey().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }


    /**
     * 查询实体集合（按主键集合）
     *
     * @param ids 主键集合
     * @return 实体集合
     */
    @Override
    public List<T> list(List<Long> ids) {
        LinkedHashMap<Long, T> map = new LinkedHashMap<>();
        List<T> listCached = getCacheBatch(ids);
        List<Long> nonCacheIds = new ArrayList<>();
        for (int i = 0; i < listCached.size(); i++) {
            if (listCached.get(i) == null) {
                nonCacheIds.add(ids.get(i));
            }
            map.put(ids.get(i), listCached.get(i));
        }
        if (nonCacheIds.size() > 0) {
            List<T> listUncached = mapper.selectBatchIds(nonCacheIds);
            //log.warn("get from db:" + getEntityName() + "," + JSON.toJSONString(listUncached));
            setCacheBatch(listUncached);
            listUncached.forEach(i -> map.put(i.getId(), i));
        }
        return new ArrayList<>(map.values())
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 查询实体集合（按查询条件）
     *
     * @param query 实体（查询条件）
     * @return 实体集合
     */
    @Override
    public List<T> list(T query) {
        List<T> results = new ArrayList<>();
        List<IdModel> models = mapper.selectIdPage(query);
        List<Long> ids = models.stream().map(IdModel::getId).collect(Collectors.toList());
        if (Objects.nonNull(ids)) {
            results = list(ids);
        }
        return results;
    }

    /**
     * 查询实体集合（按查询条件）
     *
     * @param query 实体（查询条件）
     * @return 实体集合
     */
    @Override
    public List<T> listNonSql(T query) {
        QueryWrapper<T> wrapper = new QueryWrapper<>(query);
        return listNoSql(wrapper);
    }


    @Override
    public List<T> listNoSql(QueryWrapper<T> wrapper) {
        List<T> results = new ArrayList<>();
        List<Object> ids = mapper.selectObjs(wrapper);
        if (Objects.nonNull(ids)) {
            results = list(ids.stream().map(i -> (Long) i).collect(Collectors.toList()));
        }
        return results;
    }

    @Override
    public List<Long> listIdsNoSql(QueryWrapper<T> wrapper) {
        List<Object> idsObj = mapper.selectObjs(wrapper);
        List<Long> ids;
        if (idsObj != null) {
            ids = idsObj.stream().map(i -> (Long) i).collect(Collectors.toList());
        } else {
            ids = new ArrayList<>();
        }
        return ids;
    }

    /**
     * 联合查询
     *
     * @param query 实体（查询条件）
     * @return 实体集合（Map）
     */
    @Override
    public List<Map<String, Object>> unionList(T query) {
        return mapper.selectUnion(query);
    }

    /**
     * 查询实体分页
     *
     * @param query      实体（查询条件）
     * @param pagination 分页实体（分页条件）
     * @return 分页实体
     */
    @Override
    public Pagination<T> page(T query, Pagination<T> pagination) {
        Page page = Pagination.toPage(pagination);
        List<IdModel> models = mapper.selectIdPage(page, query);
        List<Long> ids = models.stream().map(IdModel::getId).collect(Collectors.toList());
        List<T> records = list(ids);
        page.setRecords(records);
        return Pagination.fromPage(page);
    }

    /**
     * 联合查询分页
     *
     * @param query      实体（查询条件）
     * @param pagination 分页实体（分页条件）
     * @return 分页实体（Map）
     */
    @Override
    public Pagination<Map<String, Object>> unionPage(T query, Pagination<T> pagination) {
        Page page = Pagination.toPage(pagination);
        List<Map<String, Object>> list = mapper.selectUnionPage(page, query);
        Pagination<Map<String, Object>> pager = new Pagination(pagination.getCurrent(), pagination.getSize());
        pager.setRecords(list);
        pager.setTotal(mapper.selectUnion(query).size());
        return pager;
    }

    /**
     * 查询实体（按主键）
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T get(Long id) {
        T result = getCache(id);
        if (result != null) return result;
        // 判断是否配置redis
        String cacheName = SpringContainer.getProperty("cacheName");
        cacheName = cacheName == null ? "" : cacheName;
        if (cacheName.equals(REDIS)) {
            // 保证redis缓存失效后,只有一个线程能读取数据库,减轻数据库压力
            Lock lock = redissonClient.getLock("dbToRedis_" + id);
            try {
                boolean tryLock = lock.tryLock(2, TimeUnit.SECONDS);
                if (tryLock) {
                    // 再次读取缓存
                    result = getCache(id);
                    if (result == null) {
                        result = mapper.selectById(id);
                        this.setCache(result);
                    }
                } else {
                    get(id);
                }
            } catch (InterruptedException ex) {
                log.error("Thread interrupted while trying to acquire lock.", ex);
                result = mapper.selectById(id);
                this.setCache(result);
            } finally {
                lock.unlock();
            }
        } else {
            result = mapper.selectById(id);
            this.setCache(result);
        }
        return result;
    }

    /**
     * 查询实体（按查询条件）
     *
     * @param query 实体（查询条件）
     * @return 实体
     */
    @Override
    public T get(T query) {
        T result = mapper.selectOne(new QueryWrapper<>(query));
        if (Objects.nonNull(result)) {
            this.setCache(result);
        }
        return result;
    }

    /**
     * 插入实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public T insert(T entity) {
        entity.prepareBeforeInsert();
        if (mapper.insert(entity) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
        transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(entity);
        return mapper.selectById(entity.getId());
    }

    /**
     * 插入实体（无返回）
     */
    @Override
    public void insertWithoutReturn(T entity) {
        entity.prepareBeforeInsert();
        if (mapper.insert(entity) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
        transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(entity);
    }


    protected SqlSessionFactory getSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            synchronized(this) {
                if (this.sqlSessionFactory == null) {
                    Object target = this.mapper;
                    if (AopUtils.isLoadSpringAop()) {
                        while(org.springframework.aop.support.AopUtils.isAopProxy(target)) {
                            target = AopProxyUtils.getSingletonTarget(target);
                        }
                    }
                    if (target instanceof MybatisMapperProxy) {
                        MybatisMapperProxy mybatisMapperProxy = (MybatisMapperProxy) Proxy.getInvocationHandler(target);
                        SqlSessionTemplate sqlSessionTemplate = (SqlSessionTemplate)mybatisMapperProxy.getSqlSession();
                        this.sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
                    } else {
                        this.sqlSessionFactory = GlobalConfigUtils.currentSessionFactory(this.entityClass);
                    }
                }
            }
        }
        return this.sqlSessionFactory;
    }

    /**
     * 批量插入实体
     */
    @Override
    public void insertBatch(List<T> entities) {
        try {
            MybatisBatch<T> mybatisBatch = new MybatisBatch<>(getSqlSessionFactory(), entities);
            MybatisBatch.Method<T> method = new MybatisBatch.Method<>(mapperClass);
            mybatisBatch.execute(method.insert());
            for (T entity : entities){
                transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(entity);
            }
        } catch (Exception e) {
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 修改实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public T update(T entity) {
        this.deleteCache(entity);
        UpdateWrapper<T> wrapper = new UpdateWrapper<>((T) entity.where());
        entity.prepareBeforeUpdate();
        if (mapper.update(entity, wrapper) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
        transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(entity);
        T t = mapper.selectById(entity.getId());
        this.deleteCache(entity);
        return t;
    }

    /**
     * 根据条件修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Wrapper<T> queryWrapper, Wrapper<T> updateWrapper) {
        List<T> recordList = mapper.selectList(queryWrapper);
        if (Objects.isNull(recordList)) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_NOT_FOUND).build());
        }
        this.deleteCacheBatch(recordList);
        if (mapper.update(null, updateWrapper) <= 0) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_PHYSICAL_DELETE_FAILED).build());
        }
        for (T record : recordList){
            transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(record);
        }
        this.deleteCacheBatch(recordList);
    }

    /**
     * 修改实体（无返回）
     */
    @Override
    public void updateWithoutReturn(T entity) {
        this.deleteCache(entity);
        UpdateWrapper<T> wrapper = new UpdateWrapper<>((T) entity.where());
        entity.prepareBeforeUpdate();
        if (mapper.update(entity, wrapper) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
        transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(entity);
        this.deleteCache(entity);
    }

    private void updateWithoutReturnBatch(T entity) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>((T) entity.where());
        entity.prepareBeforeUpdate();
        if (mapper.update(entity, wrapper) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 批量修改实体
     */
    @Override
    public void updateBatch(List<T> entities) {
        try {
            MybatisBatch<T> mybatisBatch = new MybatisBatch<>(getSqlSessionFactory(), entities);
            MybatisBatch.Method<T> method = new MybatisBatch.Method<>(mapperClass);
            mybatisBatch.execute(method.updateById());
            this.deleteCacheBatch(entities);
            for (T entity : entities){
                transactionalInvoker.setGlobalTCAndLocalTCCacheKeys(entity);
            }
        } catch (Exception e) {
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 插入或修改实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public T insertOrUpdate(T entity) {
        if (entity.getId() == null) {
            this.insert(entity);
        } else {
            this.update(entity);
        }
        return entity;
    }

    /**
     * 逻辑删除实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDelete(Long id) {
        T record = this.get(id);
        if (Objects.isNull(record)) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_NOT_FOUND).build());
        }
        this.deleteCache(record);
        if (mapper.deleteById(id) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_LOGIC_DELETE_FAILED).build());
        }
        this.deleteCache(record);
    }

    /**
     * 批量逻辑删除实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDeleteBatch(List<Long> ids) {
        try {
            List<T> entities = new ArrayList<>();
            for (Long id : ids) {
                T record = currentModelClass().newInstance();
                record.setId(id);
                entities.add(record);
            }
            MybatisBatch<T> mybatisBatch = new MybatisBatch<>(getSqlSessionFactory(), entities);
            MybatisBatch.Method<T> method = new MybatisBatch.Method<>(mapperClass);
            mybatisBatch.execute(method.deleteById());
            this.deleteCacheBatch(entities);
        } catch (Exception e) {
            throw new DataAccessException(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
        }
    }

    /**
     * 物理删除实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void physicalDelete(Long id) {
        T record = this.get(id);
        if (Objects.isNull(record)) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_NOT_FOUND).build());
        }
        this.deleteCache(record);
        if (mapper.physicalDelete(id) != 1) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_PHYSICAL_DELETE_FAILED).build());
        }
    }

    /**
     * 批量物理删除实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void physicalDeleteBatch(List<Long> ids) {
        List<T> records = list(ids);
        if (Objects.isNull(records) || records.size() == 0) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_NOT_FOUND).build());
        }
        this.deleteCacheBatch(records);
        if (mapper.deleteBatchIds(records.stream().map(T::getId).collect(Collectors.toList())) == 0) {
            throw new DataAccessException(Error.builder().responseCode(ResponseCode.DATA_ACCESS_PHYSICAL_DELETE_FAILED).build());
        }
    }

    protected Class<T> currentModelClass() {
        return BeanRefUtil.getSuperClassGenricType(getClass(), 0);
    }

    protected Class<T> currentMapperClass() {
        return BeanRefUtil.getSuperClassGenricType(getClass(), 1);
    }

}

