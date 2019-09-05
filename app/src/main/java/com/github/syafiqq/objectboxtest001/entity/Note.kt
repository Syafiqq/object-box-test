package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class Note(
    @Id var id: Long?,
    var text: String?,
    var date: Date?
)