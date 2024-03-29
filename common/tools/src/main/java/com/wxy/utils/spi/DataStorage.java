package com.wxy.utils.spi;

/**
 * SPI （服务发现机制）接口示例
 * SPI 的本质是将接口实现类的全限定名配置在文件中，并由服务加载器读取配置文件，加载实现类。
 */
public interface DataStorage {

    String search(String key);

}
