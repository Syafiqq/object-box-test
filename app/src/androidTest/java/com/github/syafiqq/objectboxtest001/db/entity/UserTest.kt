package com.github.syafiqq.objectboxtest001.db.entity

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.User
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var userDao: Box<User>? = null
    private var userEntity: User? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context?.let {
            db = MyObjectBox.builder()
                .androidContext(it)
                .build()
        }
        userDao = db?.boxFor(User::class.java)
        userEntity = User(
            id = null,
            name = "This is text",
            status = "active"
        )
        assertThat(userEntity, `is`(notNullValue()))
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
    fun it_should_select_all_entities() {
        val entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(0)))
    }

    @Test
    fun it_should_insert_user_with_null() {
        userEntity?.let {
            it.id = null
            userDao?.put(it)
        }

        val entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(1)))
        entities?.forEach {
            assertThat(it?.id, `is`(CoreMatchers.equalTo(userEntity?.id)))
            assertThat(it?.id, `is`(CoreMatchers.equalTo(1L)))
            assertThat(it?.name, `is`(CoreMatchers.equalTo(userEntity?.name)))
            assertThat(it?.status, `is`(CoreMatchers.equalTo(userEntity?.status)))
        }
    }

    @Test
    fun it_should_insert_user_with_zero() {
        userEntity?.let {
            it.id = 0L
            userDao?.put(it)
        }

        val entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(1)))
        entities?.forEach {
            assertThat(it?.id, `is`(CoreMatchers.equalTo(userEntity?.id)))
            assertThat(it?.id, `is`(CoreMatchers.equalTo(1L)))
            assertThat(it?.name, `is`(CoreMatchers.equalTo(userEntity?.name)))
            assertThat(it?.status, `is`(CoreMatchers.equalTo(userEntity?.status)))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun it_should_not_insert_user_with_greater_than_internal_sequence() {
        userEntity?.let {
            it.id = 10L
            userDao?.put(it)
        }
    }

    @Test
    fun it_should_delete_user() {
        userEntity?.let { userDao?.put(it) }

        var entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(1)))

        entities?.forEach {
            assertThat(it?.id, `is`(CoreMatchers.equalTo(1L)))
        }

        userEntity?.let { userDao?.remove(it) }

        entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(0)))
    }

    @Test
    fun it_should_update_name() {
        val newText = "New Text"
        assertThat(newText, `is`(CoreMatchers.not(CoreMatchers.equalTo(userEntity?.name))))

        userEntity?.let { userDao?.put(it) }

        var entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(1)))
        assertThat(entities?.first()?.name, `is`(CoreMatchers.equalTo(userEntity?.name)))

        userEntity?.name = newText
        userEntity?.let { userDao?.put(it) }

        entities = userDao?.all

        assertThat(entities, `is`(CoreMatchers.instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(CoreMatchers.equalTo(1)))
        assertThat(entities?.first()?.name, `is`(CoreMatchers.equalTo(newText)))
    }
}