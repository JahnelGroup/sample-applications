package com.jahnelgroup.jgbay.common.context

import com.jahnelgroup.jgbay.data.user.User

interface UserContextService {

    fun getCurrentUser() : User?
    fun getCurrentUserId() : Long?
    fun getCurrentUsername() : String?

}