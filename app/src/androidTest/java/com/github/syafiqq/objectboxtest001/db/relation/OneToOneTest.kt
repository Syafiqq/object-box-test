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
class NoteTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var noteDao: Box<Note>? = null
    private var userDao: Box<User>? = null
    private var noteEntity: Note? = null
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
        noteEntity = Note(
            id = null,
            text = "This is text",
            date = Date()
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

        assertThat(noteDao?.count(), `is`(equalTo(1L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))
    }

    @Test
    fun it_should_attached_via_update() {
        noteEntity?.let { noteDao?.put(it) }
        userEntity?.let { userDao?.put(it) }

        assertThat(noteEntity?.id, `is`(equalTo(1L)))
        assertThat(userEntity?.id, `is`(equalTo(1L)))
        assertThat(noteDao?.count(), `is`(equalTo(1L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))

        assertThat(noteEntity?.user?.target, `is`(nullValue()))
        assertThat(noteEntity?.user?.target?.id, `is`(nullValue()))
        assertThat(noteEntity?.userId, `is`(nullValue()))

        noteEntity?.user?.target = userEntity
        noteEntity?.let { noteDao?.put(it) }

        assertThat(noteEntity?.id, `is`(equalTo(1L)))
        assertThat(userEntity?.id, `is`(equalTo(1L)))
        assertThat(noteDao?.count(), `is`(equalTo(1L)))
        assertThat(userDao?.count(), `is`(equalTo(1L)))

        assertThat(noteEntity?.user?.target, `is`(notNullValue()))
        assertThat(noteEntity?.user?.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
        assertThat(noteEntity?.userId, `is`(equalTo(1L)))

        val noteEntities = noteDao?.all
        val actualNote1 = noteEntities?.first()

        assertThat(actualNote1, `is`(notNullValue()))
        assertThat(actualNote1?.user?.target, `is`(equalTo(userEntity)))
        assertThat(actualNote1?.user?.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
        assertThat(actualNote1?.userId, `is`(equalTo(userEntity?.id)))

        val userEntities = userDao?.all
        val actualUser2 = userEntities?.first()
        assertThat(actualUser2, `is`(notNullValue()))
        assertThat(actualUser2?.notes, `is`(notNullValue()))
        assertThat(actualUser2?.notes?.size, `is`(equalTo(1)))
        actualUser2?.notes?.forEach {n ->
            assertThat(n?.user, `is`(notNullValue()))
            assertThat(n?.user?.target, `is`(notNullValue()))
            assertThat(n?.user?.target?.id, `is`(allOf(notNullValue(), equalTo(1L))))
            assertThat(n?.userId, `is`(equalTo(1L)))
        }
    }
}