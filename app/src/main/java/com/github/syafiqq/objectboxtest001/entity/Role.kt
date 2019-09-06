package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class Role(
    @Id var id: Long? = null,
    var name: String? = null
) {
    @Backlink(to = "roles")
    lateinit var users: ToMany<User>

    constructor(id: Long? = null,
                name: String? = null,
                vararg users: User) : this(id, name) {
        assignUsers(users.asIterable())
    }

    constructor(id: Long? = null,
                name: String? = null,
                users: Iterable<User>) : this(id, name) {
        assignUsers(users)
    }

    private fun assignUsers(users: Iterable<User>) {
        users.forEach { this.users.add(it) }
    }
}