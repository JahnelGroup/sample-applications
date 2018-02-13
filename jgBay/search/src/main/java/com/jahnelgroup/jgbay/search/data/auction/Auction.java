package com.jahnelgroup.jgbay.search.data.auction;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document(indexName = "auctions", refreshInterval = "-1")
public class Auction {

    @Id
    private Long id;
    private String status;
    private String title;
    private Long sellerId;
    private Short numberOfBids;
    private Set<String> categories = new HashSet<>();

}
