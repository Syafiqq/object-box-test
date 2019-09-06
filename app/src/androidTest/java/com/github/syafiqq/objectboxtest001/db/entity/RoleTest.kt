package com.github.syafiqq.objectboxtest001.db.entity

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.Role
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoleTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var roleDao: Box<Role>? = null
    private var roleEntity: Role? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context?.let {
            db = MyObjectBox.builder()
                .androidContext(it)
                .build()
        }
        roleDao = db?.boxFor(Role::class.java)
        roleEntity = Role(
            id = null,
            name = "This is text"
        )
        assertThat(roleEntity, `is`(notNullValue()))
        assertThat(roleDao, `is`(notNullValue()))
        assertThat(db, `is`(notNullValue()))
        assertThat(context, `is`(notNullValue()))
    }

    @After
    fun tearDown() {
        roleDao?.removeAll()
        db?.close()
    }


    @Test
    fun it_should_select_all_entities() {
        val entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(0)))
    }

    @Test
    fun it_should_insert_role_with_null() {
        roleEntity?.let {
            it.id = null
            roleDao?.put(it)
        }

        val entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        entities?.forEach {
            assertThat(it?.id, `is`(equalTo(roleEntity?.id)))
            assertThat(it?.id, `is`(equalTo(1L)))
            assertThat(it?.name, `is`(equalTo(roleEntity?.name)))
        }
    }

    @Test
    fun it_should_insert_role_with_zero() {
        roleEntity?.let {
            it.id = 0L
            roleDao?.put(it)
        }

        val entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        entities?.forEach {
            assertThat(it?.id, `is`(equalTo(roleEntity?.id)))
            assertThat(it?.id, `is`(equalTo(1L)))
            assertThat(it?.name, `is`(equalTo(roleEntity?.name)))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun it_should_not_insert_role_with_greater_than_internal_sequence() {
        roleEntity?.let {
            it.id = 10L
            roleDao?.put(it)
        }
    }

    @Test
    fun it_should_delete_role() {
        roleEntity?.let { roleDao?.put(it) }

        var entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))

        entities?.forEach {
            assertThat(it?.id, `is`(equalTo(1L)))
        }

        roleEntity?.let { roleDao?.remove(it) }

        entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(0)))
    }

    @Test
    fun it_should_update_name() {
        val newText = "New Text"
        assertThat(newText, `is`(not(equalTo(roleEntity?.name))))

        roleEntity?.let { roleDao?.put(it) }

        var entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        assertThat(entities?.first()?.name, `is`(equalTo(roleEntity?.name)))

        roleEntity?.name = newText
        roleEntity?.let { roleDao?.put(it) }

        entities = roleDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        assertThat(entities?.first()?.name, `is`(equalTo(newText)))
    }
}