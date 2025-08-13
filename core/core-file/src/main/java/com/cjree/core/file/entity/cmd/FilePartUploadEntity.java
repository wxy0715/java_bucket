package com.cjree.core.file.entity.cmd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "分片上传")
public class FilePartUploadEntity {

    @Schema(description = "url", required = true)
    private String url = "";


    @Schema(description = "索引", required = true)
    private Integer index;

    @JSONField(serialize = false)
    @Schema(description = "文件", required = true)
    private MultipartFile file;
}
