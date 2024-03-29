package com.wxy.seata1.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钱包
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    /**
     * 名称
     */
    private String name;
    /**
     * 各种货币
     */
    private List<Currency> currencyList;
}
