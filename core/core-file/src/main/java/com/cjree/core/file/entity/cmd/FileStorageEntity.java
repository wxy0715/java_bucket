package com.cjree.core.file.entity.cmd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Schema(description = "文件上传实体")
public class FileStorageEntity {
    @Schema(description = "路径", required = false)
    private String path = "";

    @Schema(description = "关联对象id", required = false)
    private String objectId = null;

    @Schema(description = "对象类型", required = false)
    private String objectType = "";

    @Schema(description = "文件名称,使用byte[]、InputStream等方式上传，无法获取originalFilename属性时设置", required = false)
    private String originalFilename = "";

    @Schema(description = "自定义属性,json格式", required = false)
    private String attr = "";

    @Schema(description = "存储平台", required = false)
    private String platform = "";

    @Schema(description = "指定缩略图是否开启")
    private Boolean thumbnailEnable = Boolean.FALSE;

    @Schema(description = "指定缩略图长度", required = false,example = "200")
    private Integer thumbnailLength = 200;

    @Schema(description = "指定缩略图宽度", required = false,example = "200")
    private Integer thumbnailWidth = 200;

    @Schema(description = "指定缩略图后缀，必须是 thumbnailator 支持的图片格式，默认使用全局的", required = false,example = ".jpg")
    private String thumbnailSuffix = ".jpg";

    @Schema(description = "指定缩略图的保存文件名，注意此文件名不含后缀，默认自动生成", required = false)
    private String thFilename = "";

    @JSONField(serialize = false)
    @Schema(description = "文件", required = true)
    private List<MultipartFile> fileList;
}
