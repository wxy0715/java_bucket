package com.cjree.core.model.common;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangxingyu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "IdCmd", description = "id/id集合传参model")
public class IdCmd {
    public interface SelectById{};
    public interface DeleteById{};

    @NotNull(message = "id传参不能为空",groups = {SelectById.class})
    @Schema(description = "id传参")
    private Long id;

    @NotEmpty(message = "id集合传参不能为空",groups = {DeleteById.class})
    @Schema(description = "id集合传参")
    private List<Long> idList = new ArrayList<Long>();
}
