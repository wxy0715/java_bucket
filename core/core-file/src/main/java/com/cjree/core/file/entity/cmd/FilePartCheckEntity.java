package com.cjree.core.file.entity.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件分片效验")
public class FilePartCheckEntity {
    @Schema(description = "主文件url")
    private String url;

    @Schema(description = "索引值")
    private Integer index;
}
