package com.github.syafiqq.objectboxtest001.db.relation

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
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
class SelfReferenceTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var userDao: Box<User>? = null
    private var user000: User? = null
    private var user010: User? = null
    private var user020: User? = null
    private var user110: User? = null
    private var user120: User? = null
    private var user210: User? = null
    private var user220: User? = null
    private var users0: MutableList<User>? = null
    private var users1: MutableList<User>? = null
    private var users2a: MutableList<User>? = null
    private var users2b: MutableList<User>? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context?.let {
            db = MyObjectBox.builder()
                .androidContext(it)
                .build()
        }
        userDao = db?.boxFor(User::class.java)
        user000 = User(null, "000", "active")
        user010 = User(null, "010", "active")
        user020 = User(null, "020", "active")
        user110 = User(null, "110", "active")
        user120 = User(null, "120", "active")
        user210 = User(null, "210", "active")
        user220 = User(null, "220", "active")
        users0 = mutableListOf(user000!!)
        users1 = mutableListOf(user010!!, user020!!)
        users2a = mutableListOf(user110!!, user120!!)
        users2b = mutableListOf(user210!!, user220!!)

        assertThat(user000, `is`(notNullValue()))
        assertThat(user010, `is`(notNullValue()))
        assertThat(user020, `is`(notNullValue()))
        assertThat(user110, `is`(notNullValue()))
        assertThat(user120, `is`(notNullValue()))
        assertThat(user210, `is`(notNullValue()))
        assertThat(user220, `is`(notNullValue()))
        assertThat(users0, `is`(notNullValue()))
        assertThat(users1, `is`(notNullValue()))
        assertThat(users2a, `is`(notNullValue()))
        assertThat(users2b, `is`(notNullValue()))
        assertThat(userDao, `is`(notNullValue()))
        assertThat(db, `is`(notNullValue()))
        assertThat(context, `is`(notNullValue()))
    }

    @After
    fun tearDown() {
        userDao?.removeAll()
        db?.close()
    }

    @Test
    fun it_should_insert_all_level() {
        users0?.let { userDao?.put(it) }
        users1?.let { userDao?.put(it) }
        users2a?.let { userDao?.put(it) }
        users2b?.let { userDao?.put(it) }

        assertThat(userDao?.count(), `is`(equalTo(7L)))
    }

    @Test
    fun it_should_attach_level0_and_level1() {
        users0?.let { userDao?.put(it) }
        users1?.let { userDao?.put(it) }

        users1?.forEach { it.parent.target = user000 }
        users1?.let { userDao?.put(it) }

        assertThat(userDao?.count(), `is`(equalTo(3L)))

        val users = userDao?.all
        val actualEntities0 = users?.first { it.id == user000?.id }
        val actualEntities1 = users?.filter { al -> users1?.map(User::id)?.contains(al.id) ?: false }

        assertThat(actualEntities0, `is`(notNullValue()))
        assertThat(actualEntities0?.children, `is`(notNullValue()))
        assertThat(actualEntities0?.children?.toMutableList(), `is`(equalTo(users1)))
        assertThat(actualEntities0?.children?.toMutableList(), `is`(equalTo(actualEntities1)))

        actualEntities1?.forEach {
            assertThat(users1?.first{e -> e.id == it.id}, `is`(notNullValue()))
            assertThat(it.parent, `is`(notNullValue()))
            assertThat(it.parentId, `is`(notNullValue()))
            assertThat(it.parent.target, `is`(allOf(equalTo(user000), equalTo(actualEntities0))))
        }
    }

    @Test
    fun it_should_attach_level1_and_level2() {
        users1?.let { userDao?.put(it) }
        users2a?.let { userDao?.put(it) }
        users2b?.let { userDao?.put(it) }

        users2a?.forEach { it.parent.target = user010 }
        users2b?.forEach { it.parent.target = user020 }
        users2a?.let { userDao?.put(it) }
        users2b?.let { userDao?.put(it) }

        assertThat(userDao?.count(), `is`(equalTo(6L)))

        val users = userDao?.all
        val actualEntities1 = users?.filter { al -> users1?.map(User::id)?.contains(al.id) ?: false }
        val actualEntities2a = users?.filter { al -> users2a?.map(User::id)?.contains(al.id) ?: false }
        val actualEntities2b = users?.filter { al -> users2b?.map(User::id)?.contains(al.id) ?: false }

        actualEntities1?.forEach {
            val user = if(it.name == "010") users2a else users2b
            val actualEntity = if(it.name == "010") actualEntities2a else actualEntities2b
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.children, `is`(notNullValue()))
            assertThat(it?.children?.toMutableList(), `is`(equalTo(user)))
            assertThat(it?.children?.toMutableList(), `is`(equalTo(actualEntity)))
        }

        mapOf(0 to actualEntities2a, 1 to actualEntities2b).forEach { i, l ->
            l?.forEach {
                val user = if(i == 0) user010 else user020
                val users = if(i == 0) users2a else users2b
                val actualEntity = if(i == 0) actualEntities1?.first { k -> k.id == user010?.id } else actualEntities1?.first { k -> k.id == user020?.id }
                assertThat(users?.first{e -> e.id == it.id}, `is`(notNullValue()))
                assertThat(it.parent, `is`(notNullValue()))
                assertThat(it.parentId, `is`(notNullValue()))
                assertThat(it.parent.target, `is`(allOf(equalTo(user), equalTo(actualEntity))))
            }
        }
    }
}