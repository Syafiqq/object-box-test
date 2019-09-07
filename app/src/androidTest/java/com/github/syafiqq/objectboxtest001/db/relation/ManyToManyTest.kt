package com.github.syafiqq.objectboxtest001.db.relation

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.Role
import com.github.syafiqq.objectboxtest001.entity.User
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManyToManyTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var roleDao: Box<Role>? = null
    private var userDao: Box<User>? = null
    private var roleEntity: MutableList<Role>? = null
    private var userEntity: MutableList<User>? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context?.let {
            db = MyObjectBox.builder()
                .androidContext(it)
                .build()
        }
        roleDao = db?.boxFor(Role::class.java)
        userDao = db?.boxFor(User::class.java)
        roleEntity =mutableListOf(
            Role().apply {
                name = "This is text 1"
            },
            Role().apply {
                name = "This is text 2"
            },
            Role().apply {
                name = "This is text 3"
            },
            Role().apply {
                name = "This is text 4"
            },
            Role().apply {
                name = "This is text 5"
            }
        )
        userEntity = mutableListOf(
            User().apply {
                name = "This is text 1"
                status = "active"
            },
            User().apply {
                name = "This is text 2"
                status = "active"
            },
            User().apply {
                name = "This is text 3"
                status = "active"
            },
            User().apply {
                name = "This is text 4"
                status = "active"
            },
            User().apply {
                name = "This is text 5"
                status = "active"
            }
        )
        assertThat(roleEntity, `is`(notNullValue()))
        assertThat(userEntity, `is`(notNullValue()))
        assertThat(roleDao, `is`(notNullValue()))
        assertThat(userDao, `is`(notNullValue()))
        assertThat(db, `is`(notNullValue()))
        assertThat(context, `is`(notNullValue()))
    }

    @After
    fun tearDown() {
        userDao?.removeAll()
        roleDao?.removeAll()
        db?.close()
    }

    @Test
    fun it_should_insert_both() {
        roleEntity?.let { roleDao?.put(it) }
        userEntity?.let { userDao?.put(it) }

        roleEntity?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
        }
        userEntity?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
        }
        assertThat(roleDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(5L)))

        roleDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.users, `is`(notNullValue()))
            assertThat(it?.users?.size, `is`(equalTo(0)))
        }
        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.roles, `is`(notNullValue()))
            assertThat(it?.roles?.size, `is`(equalTo(0)))
        }
    }

    @Test
    fun it_should_attached_all_via_insert() {
        roleEntity?.let { roleDao?.put(it) }
        userEntity?.let { userDao?.put(it) }
        roleEntity?.forEach { r ->
            userEntity?.let { 
                r.users.addAll(it)
            }
        }
        roleEntity?.let { roleDao?.put(it) }

        assertThat(roleDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(5L)))

        roleDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.users, `is`(notNullValue()))
            assertThat(it?.users?.size, `is`(equalTo(5)))
            it?.users?.forEach { u ->
                assertThat(u, `is`(notNullValue()))
                assertThat(u?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
                assertThat(u?.roles, `is`(notNullValue()))
                assertThat(u?.roles?.size, `is`(equalTo(5)))
            }
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.roles, `is`(notNullValue()))
            assertThat(it?.roles?.size, `is`(equalTo(5)))
            it?.roles?.forEach { r ->
                assertThat(r, `is`(notNullValue()))
                assertThat(r?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
                assertThat(r?.users, `is`(notNullValue()))
                assertThat(r?.users?.size, `is`(equalTo(5)))
            }
        }
    }

    @Test
    fun it_should_attached_all_via_transaction() {
        db?.runInTx {
            try {
                roleEntity?.forEach { r ->
                    userEntity?.let {
                        r.users.addAll(it)
                    }
                }
                roleEntity?.let { roleDao?.put(it) }
            } catch (ex: Exception) {
            }
        }

        assertThat(roleDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(5L)))

        roleDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.users, `is`(notNullValue()))
            assertThat(it?.users?.size, `is`(equalTo(5)))
            it?.users?.forEach { u ->
                assertThat(u, `is`(notNullValue()))
                assertThat(u?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
                assertThat(u?.roles, `is`(notNullValue()))
                assertThat(u?.roles?.size, `is`(equalTo(5)))
            }
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.roles, `is`(notNullValue()))
            assertThat(it?.roles?.size, `is`(equalTo(5)))
            it?.roles?.forEach { r ->
                assertThat(r, `is`(notNullValue()))
                assertThat(r?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
                assertThat(r?.users, `is`(notNullValue()))
                assertThat(r?.users?.size, `is`(equalTo(5)))
            }
        }
    }

    @Test
    fun it_should_not_raised_inconsistency() {
        roleEntity?.forEach { r ->
            userEntity?.let {
                r.users.addAll(it)
            }
        }
        roleEntity?.let { roleDao?.put(it) }

        assertThat(roleDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(5L)))

        roleDao?.removeAll()

        assertThat(roleDao?.count(), `is`(equalTo(0L)))
        assertThat(userDao?.count(), `is`(equalTo(5L)))

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it?.roles, `is`(notNullValue()))
            assertThat(it?.roles?.size, `is`(equalTo(0)))
        }

        userDao?.removeAll()

        assertThat(roleDao?.count(), `is`(equalTo(0L)))
        assertThat(userDao?.count(), `is`(equalTo(0L)))
    }
}