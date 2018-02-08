package com.jahnelgroup.jgbay.search.data.auction

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface UserRepo : ElasticsearchRepository<User, Long>
