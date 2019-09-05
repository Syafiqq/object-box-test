package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class Note(
    @Id var id: Long? = null,
    var text: String? = null,
    var date: Date? = null
) {
    lateinit var user: ToOne<User>
}