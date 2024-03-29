package com.wxy.arthas.api;

import com.wxy.arthas.entity.BusinessProjectResult;
import com.wxy.arthas.entity.GetProjectBudgetData;
import com.wxy.arthas.entity.ProjectControlData;
import com.wxy.arthas.rpc.client.BusinessClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@Slf4j
public class IndexController {

    @Resource
    private BusinessClient businessClient;

    @PostMapping("/index")
    public List<ProjectControlData> index() {
        GetProjectBudgetData getProjectBudgetData = new GetProjectBudgetData();
        getProjectBudgetData.setPageNO(1);
        getProjectBudgetData.setPageSize(10);
        BusinessProjectResult jsonObject = businessClient.existProject(getProjectBudgetData);
        return jsonObject.convertList(ProjectControlData.class);
    }

}
