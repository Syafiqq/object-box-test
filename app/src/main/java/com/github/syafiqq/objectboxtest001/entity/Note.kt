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
    var userId: Long = 0
    lateinit var user: ToOne<User>

    constructor(id: Long? = null,
                text: String? = null,
                date: Date? = null,
                userId: Long) : this(id, text, date) {
        user.targetId = userId
    }

    constructor(id: Long? = null,
                text: String? = null,
                date: Date? = null,
                user: User) : this(id, text, date) {
        this.user.target = user
    }
}