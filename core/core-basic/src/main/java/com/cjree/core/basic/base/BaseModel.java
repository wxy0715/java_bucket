package com.cjree.core.basic.base;

import com.cjree.core.model.base.BaseCoreModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础Model，所有的model都要继承此model todo 登录人信息
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class BaseModel extends BaseCoreModel implements Serializable {
    /**
     * 插入数据前的准备工作
     */
    public void prepareBeforeInsert() {
        try {
            //this.setCreator(currentUser);
        } catch (Exception e) {
            this.setCreator(0L);
        }
    }

    /**
     * 插入数据前的准备工作
     */
    public void prepareBeforeInsert(Long id) {
        this.setCreator(id);
    }

    /**
     * 更新数据前的准备工作
     */
    public void prepareBeforeUpdate() {
        try{

        } catch (Exception e) {
            this.setUpdater(0L);
        }
        Date currentDate = new Date();
        this.setVersionDate(currentDate);
        this.setUpdateDate(currentDate);
    }

    /**
     * 更新数据前的准备工作
     */
    public void prepareBeforeUpdate(Long id) {
        this.setUpdater(id);
        Date currentDate = new Date();
        this.setVersionDate(currentDate);
        this.setUpdateDate(currentDate);
    }
}
