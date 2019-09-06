package com.github.syafiqq.objectboxtest001.entity

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class User(
    @Id var id: Long? = null,
    var name: String? = null,
    var status: String? = null
) {
    var parentId: Long = 0
    lateinit var parent: ToOne<User>
    @Backlink(to = "parent")
    lateinit var children: ToMany<User>
    @Backlink(to = "users")
    lateinit var roles: ToMany<Role>
    @Backlink(to = "user")
    lateinit var notes: ToMany<Note>

    constructor(id: Long? = null,
                name: String? = null,
                status: String? = null,
                parent: User? = null,
                children: Iterable<User> = listOf(),
                roles: Iterable<Role> = listOf(),
                notes: Iterable<Note> = listOf()) : this(id, name, status) {
        this.parent.target = parent
        children.forEach { this.children.add(it) }
        roles.forEach { this.roles.add(it) }
        notes.forEach { this.notes.add(it) }
    }
}