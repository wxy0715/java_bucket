package com.cjree.jdk17Template.dto.cmd;


import com.cjree.core.model.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "CreateExampleCmd", description = "创建示例")
public class CreateExampleCmd {

    @Schema(description = "id")
    @NotNull(message = "id不能为空", groups = Update.class)
    private Long id;

    @Schema(description = "code")
    @NotNull(message = "code不能为空")
    private String code;

    @Schema(description = "name")
    @NotNull(message = "name不能为空")
    private String name;

    @Schema(description = "personName")
    private String personName;

    @Schema(description = "icCode")
    private String icCode;
}
