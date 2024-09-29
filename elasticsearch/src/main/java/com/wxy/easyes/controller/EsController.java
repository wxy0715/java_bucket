package com.wxy.easyes.controller;

import com.wxy.easyes.document.Document;
import com.wxy.easyes.esmapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * es操作
 * @author wangxingyu
 * @date 2023/05/04 17:04:47
 */
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
@RestController
public class EsController {
    @Resource
    DocumentMapper documentMapper;

    /**
     * 查询(指定)
     *
     * @param title 标题
     */
    @GetMapping("/select")
    public Document select(String title) {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, title);
        return documentMapper.selectOne(wrapper);
    }

    /**
     * 搜索(模糊)
     *
     * @param key 搜索关键字
     */
    @GetMapping("/search")
    public List<Document> search(String key) {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.like(Document::getTitle, key);
        return documentMapper.selectList(wrapper);
    }

    /**
     * 插入
     */
    @PostMapping("/insert")
    public Integer insert(@RequestBody Document document) {
        return documentMapper.insert(document);
    }

    /**
     * 插入
     */
    @PostMapping("/insertTest")
    public Integer insertTest() {
        List<Document> documents = generateDocuments(100);
        return documentMapper.insertBatch(documents);
    }

    public static List<Document> generateDocuments(int count) {
        List<Document> documents = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Document document = new Document();
            document.setId((long) i);
            document.setMoney(BigDecimal.valueOf(Math.random() * 1000));
            document.setType((int) (Math.random() * 10));
            document.setTitle("Title " + i);
            document.setContent("Content " + i);
            document.setHighlightContent("Highlight Content " + i);
            document.setScope((float) (Math.random() * 100));
            document.setIpAddress("192.168.0." + (i % 255));

            documents.add(document);
        }
        return documents;
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Integer update(@RequestBody Document document) {
        // 测试更新 更新有两种情况 分别演示如下:
        // case1: 已知id, 根据id更新 (为了演示方便,此id是从上一步查询中复制过来的,实际业务可以自行查询)
        return documentMapper.updateById(document);

        // case2: id未知, 根据条件更新
//        LambdaEsUpdateWrapper<Document> wrapper = new LambdaEsUpdateWrapper<>();
//        wrapper.like(Document::getTitle, document.getTitle());
//        Document document2 = new Document();
//        document2.setTitle(document.getTitle());
//        document2.setContent(document.getContent());
//        documentMapper.update(document2, wrapper);

    }

    /**
     * 删除
     *
     * @param id 主键
     */
    @DeleteMapping("/delete/{id}")
    public Integer delete(@PathVariable String id) {
        // 测试删除数据 删除有两种情况:根据id删或根据条件删
        return documentMapper.deleteById(id);
    }

}
