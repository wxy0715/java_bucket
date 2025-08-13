package com.cjree.jdk17Template.dto.qry;

import com.cjree.core.model.validate.QueryPage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ExampleQry", description = "查询示例")
public class ExampleQry {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "code")
    private String code;

    @Schema(description = "name")
    private String name;

    @NotNull(message = "pageIndex不能为空", groups = QueryPage.class)
    @Schema(description = "pageIndex")
    private Integer pageIndex;

    @NotNull(message = "pageSize不能为空", groups = QueryPage.class)
    @Schema(description = "pageSize")
    private Integer pageSize;
}
