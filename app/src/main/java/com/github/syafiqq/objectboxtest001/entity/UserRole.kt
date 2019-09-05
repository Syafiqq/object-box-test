package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class UserRole(
    @Id
    var id: Long?,
    var userId: Long?,
    var roleId: Long?
)