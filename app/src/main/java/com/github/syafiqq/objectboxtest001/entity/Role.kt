package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Role(
    @Id var id: Long?,
    var name: String?
)