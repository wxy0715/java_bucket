package com.wxy.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "account")
public class Account implements Serializable {
    @MongoId
    @Id
    private Long id;

    private Long projectId;

    private Long limitId;

    private Long accountId;

    private String loan;
}
