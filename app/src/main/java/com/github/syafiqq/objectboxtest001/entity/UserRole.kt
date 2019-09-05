package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class UserRole(
    @Id var id: Long? = null,
    var userId: Long? = null,
    var roleId: Long? = null
)