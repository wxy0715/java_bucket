package com.wxy.arthas.rpc.client;

import com.dtflys.forest.annotation.*;
import com.wxy.arthas.entity.BusinessProjectResult;
import com.wxy.arthas.entity.GetProjectBudgetData;
import com.wxy.arthas.entity.ProjectControlData;

@BaseRequest(
        baseURL = "#{host}",
        headers = {"Accept:application/json"})
@DecompressGzip // 启用gzip解压
public interface BusinessClient {

    @Post(url = "/ExternalInterface/BusinessProject/GetPojectBudgetData")
    @Retry(maxRetryCount = "2", maxRetryInterval = "10")
    BusinessProjectResult existProject(@JSONBody GetProjectBudgetData projectCode);
}