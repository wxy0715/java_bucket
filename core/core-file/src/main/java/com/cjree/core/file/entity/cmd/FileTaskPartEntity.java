package com.cjree.core.file.entity.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "初始化分片任务")
public class FileTaskPartEntity {
    @Schema(description = "路径", required = false)
    private String path = "/";

    @Schema(description = "关联对象id", required = false)
    private String objectId = null;

    @Schema(description = "对象类型", required = false)
    private String objectType = "";

    @Schema(description = "自定义属性,json格式", required = false)
    private String attr = "";

    @Schema(description = "文件名称,使用byte[]、InputStream等方式上传，无法获取originalFilename属性时设置", required = true)
    private String originalFilename = "";

    @Schema(description = "文件md5", required = true)
    private String md5 = "";

    @Schema(description = "文件大小", required = true)
    private Long length;

    @Schema(description = "存储平台", required = false)
    private String platform = "";
}
