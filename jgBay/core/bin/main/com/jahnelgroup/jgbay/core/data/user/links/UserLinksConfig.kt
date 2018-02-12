package com.jahnelgroup.jgbay.core.data.user.links

import com.jahnelgroup.jgbay.core.context.UserContextService
import com.jahnelgroup.jgbay.core.data.user.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.hateoas.Resource
import org.springframework.hateoas.ResourceProcessor
import org.springframework.hateoas.Resources

/**
 * Add custom links to Resource's with a ResourceProcessor
 *
 * https://docs.spring.io/spring-data/jgbay/docs/current/reference/html/#_programmatic_links
 */
@Configuration
class UserLinksConfig (
    private val entityLinks: RepositoryEntityLinks,
    private val userContextService: UserContextService
) {

    /**
     * Add a link to be displayed when listing a collection of User's
     *
     *  Example: /api/users
     */
    @Bean
    fun userCollectionLinks() = ResourceProcessor<Resources<Resource<User>>> { res ->
        res.add(entityLinks.linkToSingleResource(userContextService.getCurrentUser()).withRel("me"))
        res
    }

}