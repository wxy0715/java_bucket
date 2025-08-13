package com.cjree.core.file.entity.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预签名地址")
public class FilePresignedOut {
    @Schema(description = "预签名url")
    private String url;

    @Schema(description = "kkFile地址")
    private String kkFileViewUrl;

    @Schema(description = "字节数组")
    private byte[] array;
}
