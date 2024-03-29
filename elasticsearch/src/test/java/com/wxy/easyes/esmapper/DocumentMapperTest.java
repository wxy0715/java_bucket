package com.wxy.easyes.esmapper;

import com.wxy.easyes.document.Document;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.core.EsWrappers;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@DisplayName("es测试")
class DocumentMapperTest {
    @Autowired
    private DocumentMapper documentMapper;

    @Test
    public void testInsert() {
        // 测试插入数据
        Document document = new Document();
        document.setTitle("老汉2");
        document.setContent("老汉1推*技术过硬1");
        int successCount = documentMapper.insert(document);
        System.out.println(successCount);
    }

    @Test
    public void testSelect() {
        // 测试查询 写法和MP一样 可以用链式,也可以非链式 根据使用习惯灵活选择即可
        String title = "老汉";
        Document document = EsWrappers.lambdaChainQuery(documentMapper)
                .eq(Document::getTitle, title)
                .one();
        System.out.println(document);
    }

    @Test
    public void search() {
        // 查询出所有标题为老汉的文档列表
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "老汉1");
        for (Document document : documentMapper.selectList(wrapper)) {
            System.out.println(document);
        }
    }

    @Test
    public void testWeight() throws IOException {
        // 测试权重
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        String keyword = "过硬";
        float contentBoost = 5.0f;
        wrapper.match(Document::getContent,keyword,contentBoost);
        SearchResponse response = documentMapper.search(wrapper);
        System.out.println(response);
    }
}