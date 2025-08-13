package com.cjree.core.basic.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class IdModel implements Serializable {
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected Long id;
}
