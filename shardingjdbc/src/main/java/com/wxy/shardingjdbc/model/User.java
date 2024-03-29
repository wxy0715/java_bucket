package com.wxy.shardingjdbc.model;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author wxy
 * @since 2023-01-07
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long userId;
}
