package com.cjree.core.model.enums;

/**
 * 删除标记
 */
public enum Available {

    /**
     * 不可用
     */
    NO,
    /**
     * 可用
     */
    YES;
    /**
     * 拿到对立的枚举
     */
    public static Available opposite(Available available) {
        return available == Available.YES ? Available.NO : Available.YES;
    }

}
