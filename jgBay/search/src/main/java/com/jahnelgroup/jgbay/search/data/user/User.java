package com.jahnelgroup.jgbay.search.data.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "users", refreshInterval = "-1")
public class User {

    @Id
    private Long id;
    private String username;
    private Short rating;

}
