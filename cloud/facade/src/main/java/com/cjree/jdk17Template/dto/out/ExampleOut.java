package com.cjree.jdk17Template.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ExampleOut", description = "返回示例")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ExampleOut {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "code")
    private String code;

    @Schema(description = "name")
    private String name;
}
