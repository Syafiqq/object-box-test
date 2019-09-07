package com.github.syafiqq.objectboxtest001.db.relation

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.Note
import com.github.syafiqq.objectboxtest001.entity.User
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class OneToManyTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var noteDao: Box<Note>? = null
    private var userDao: Box<User>? = null
    private var noteEntity: MutableList<Note>? = null
    private var userEntity: User? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context?.let {
            db = MyObjectBox.builder()
                .androidContext(it)
                .build()
        }
        noteDao = db?.boxFor(Note::class.java)
        userDao = db?.boxFor(User::class.java)
        noteEntity =mutableListOf(
            Note().apply {
                text = "This is text 1"
                date = Date()
            },
            Note().apply {
                text = "This is text 2"
                date = Date()
            },
            Note().apply {
                text = "This is text 3"
                date = Date()
            },
            Note().apply {
                text = "This is text 4"
                date = Date()
            },
            Note().apply {
                text = "This is text 5"
                date = Date()
            }
        )
        userEntity = User(
            id = null,
            name = "This is text",
            status = "active"
        )
        assertThat(noteEntity, `is`(notNullValue()))
        assertThat(userEntity, `is`(notNullValue()))
        assertThat(noteDao, `is`(notNullValue()))
        assertThat(userDao, `is`(notNullValue()))
        assertThat(db, `is`(notNullValue()))
        assertThat(context, `is`(notNullValue()))
    }

    @After
    fun tearDown() {
        userDao?.removeAll()
        noteDao?.removeAll()
        db?.close()
    }

    @Test
    fun it_should_insert_both() {
        noteEntity?.let { noteDao?.put(it) }
        userEntity?.let { userDao?.put(it) }

        assertThat(userEntity?.id, `is`(equalTo(1L)))
        noteEntity?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
        }
        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))

        noteDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(nullValue()))
            assertThat(it.user.target?.id, `is`(nullValue()))
            assertThat(it.userId, `is`(equalTo(0L)))
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it?.notes, `is`(notNullValue()))
            assertThat(it?.notes?.size, `is`(equalTo(0)))
        }
    }

    @Test
    fun it_should_attached_all_via_update() {
        noteEntity?.let { noteDao?.put(it) }
        userEntity?.let { userDao?.put(it) }
        noteEntity?.forEach {it.user.target = userEntity}
        noteEntity?.let { noteDao?.put(it) }

        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))

        noteDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it?.notes, `is`(notNullValue()))
            assertThat(it?.notes?.size, `is`(equalTo(5)))
        }
    }

    @Test
    fun it_should_attached_all_via_transaction() {

        db?.runInTx {
            try {
                noteEntity?.forEach {it.user.target = userEntity}
                noteEntity?.let { noteDao?.put(it) }
            } catch (ex: Exception) {
            }
        }

        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))

        noteDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it?.notes, `is`(notNullValue()))
            assertThat(it?.notes?.size, `is`(equalTo(5)))
        }
    }



    @Test
    fun it_should_update_list_after_insert() {
        val subNoteEntity1 = noteEntity?.drop(0)?.take(2)
        subNoteEntity1?.forEach {it.user.target = userEntity}
        subNoteEntity1?.let { noteDao?.put(it) }

        assertThat(noteDao?.count(), `is`(equalTo(2L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))
        assertThat(userEntity?.id, `is`(equalTo(1L)))
        subNoteEntity1?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L))))
        }

        val userEntities0 = userDao?.all
        val actualUserE10 = userEntities0?.first()
        assertThat(actualUserE10, `is`(notNullValue()))
        assertThat(actualUserE10?.notes, `is`(notNullValue()))
        assertThat(actualUserE10?.notes?.size, `is`(equalTo(2)))
        actualUserE10?.notes?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }

        val subNoteEntity2 = noteEntity?.drop(2)?.take(3)
        subNoteEntity2?.forEach {it.user.target = userEntity}
        subNoteEntity2?.let { noteDao?.put(it) }

        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        subNoteEntity2?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(3L), equalTo(4L), equalTo(5L))))
        }

        assertThat(actualUserE10, `is`(notNullValue()))
        assertThat(actualUserE10?.notes, `is`(notNullValue()))
        assertThat(actualUserE10?.notes?.size, `is`(equalTo(2)))

        actualUserE10?.notes?.reset()

        assertThat(actualUserE10, `is`(notNullValue()))
        assertThat(actualUserE10?.notes, `is`(notNullValue()))
        assertThat(actualUserE10?.notes?.size, `is`(equalTo(5)))
        actualUserE10?.notes?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }

        noteDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it?.notes, `is`(notNullValue()))
            assertThat(it?.notes?.size, `is`(equalTo(5)))
        }
    }


    @Test
    fun it_should_update_list_after_switch() {
        val subNoteEntity1 = noteEntity?.drop(0)?.take(2)
        subNoteEntity1?.forEach {it.user.target = userEntity}
        subNoteEntity1?.let { noteDao?.put(it) }

        assertThat(noteDao?.count(), `is`(equalTo(2L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))
        assertThat(userEntity?.id, `is`(equalTo(1L)))
        subNoteEntity1?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L))))
        }

        val userEntities0 = userDao?.all
        val actualUserE10 = userEntities0?.first()
        assertThat(actualUserE10, `is`(notNullValue()))
        assertThat(actualUserE10?.notes, `is`(notNullValue()))
        assertThat(actualUserE10?.notes?.size, `is`(equalTo(2)))
        actualUserE10?.notes?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }


        val subNoteEntity2 = noteEntity?.drop(2)?.take(3)
        subNoteEntity1?.forEach {it.user.target = null}
        subNoteEntity2?.forEach {it.user.target = userEntity}
        noteDao?.store?.runInTx {
            subNoteEntity2?.let { noteDao?.put(it) }
            subNoteEntity1?.let { noteDao?.put(it) }
        }

        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        subNoteEntity2?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(3L), equalTo(4L), equalTo(5L))))
        }

        assertThat(actualUserE10, `is`(notNullValue()))
        assertThat(actualUserE10?.notes, `is`(notNullValue()))
        assertThat(actualUserE10?.notes?.size, `is`(equalTo(2)))

        actualUserE10?.notes?.reset()

        assertThat(actualUserE10, `is`(notNullValue()))
        assertThat(actualUserE10?.notes, `is`(notNullValue()))
        assertThat(actualUserE10?.notes?.size, `is`(equalTo(3)))
        actualUserE10?.notes?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(notNullValue()))
            assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
        }

        noteDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            if(it.id ?: 0 < 3) {
                assertThat(it.user.target, `is`(nullValue()))
                assertThat(it.user.target?.id, `is`(anyOf(nullValue(), equalTo(0L))))
                assertThat(it.userId, `is`(anyOf(nullValue(), equalTo(0L))))
            } else {
                assertThat(it.user.target, `is`(notNullValue()))
                assertThat(it.user.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
                assertThat(it.userId, `is`(allOf(notNullValue(), equalTo(1L))))
            }
        }

        userDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(it?.notes, `is`(notNullValue()))
            assertThat(it?.notes?.size, `is`(equalTo(3)))
        }
    }

    @Test
    fun it_should_not_raised_inconsistency() {
        noteEntity?.forEach {it.user.target = userEntity}
        noteEntity?.let { noteDao?.put(it) }

        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))
        assertThat(userEntity?.id, `is`(equalTo(1L)))
        noteEntity?.forEach {
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
        }

        userEntity?.let { userDao?.remove(it) }

        assertThat(noteDao?.count(), `is`(equalTo(5L)))
        assertThat(userDao?.count(), `is`(equalTo(0L)))
        assertThat(userDao?.all?.size, `is`(equalTo(0)))

        noteDao?.all?.forEach {
            assertThat(it, `is`(notNullValue()))
            assertThat(it.id, `is`(anyOf(equalTo(1L), equalTo(2L), equalTo(3L), equalTo(4L), equalTo(5L))))
            assertThat(it.user.target, `is`(nullValue()))
            assertThat(it.user.target?.id, `is`(anyOf(nullValue(), equalTo(0L))))
            assertThat(it.userId, `is`(anyOf(nullValue(), equalTo(0L))))
        }
    }
}