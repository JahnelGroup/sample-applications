package com.jahnelgroup.jgbay.search.data.user;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepo extends ElasticsearchRepository<User, Long> {

}
