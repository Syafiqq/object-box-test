package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class User(
    @Id var id: Long? = null,
    var name: String? = null,
    var status: String? = null,
    var parentId: Long? = null
)