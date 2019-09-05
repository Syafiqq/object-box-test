package com.github.syafiqq.objectboxtest001.db.entity

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.Note
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
    private var noteEntity: Note? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context?.let {
            db = MyObjectBox.builder()
                .androidContext(it)
                .build()
        }
        noteDao = db?.boxFor(Note::class.java)
        noteEntity = Note(
            id = null,
            text = "This is text",
            date = Date()
        )
        assertThat(noteEntity, `is`(notNullValue()))
        assertThat(noteDao, `is`(notNullValue()))
        assertThat(db, `is`(notNullValue()))
        assertThat(context, `is`(notNullValue()))
    }

    @After
    fun tearDown() {
        noteDao?.removeAll()
        db?.close()
    }

    @Test
    fun it_should_select_all_entities() {
        val entities = noteDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(0)))
    }

    @Test
    fun it_should_insert_note() {
        noteEntity?.let { noteDao?.put(it) }

        val entities = noteDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        entities?.forEach {
            assertThat(it?.id, `is`(equalTo(noteEntity?.id)))
            assertThat(it?.id, `is`(equalTo(1L)))
            assertThat(it?.text, `is`(equalTo(noteEntity?.text)))
            assertThat(it?.date, `is`(equalTo(noteEntity?.date)))
        }
    }

     @Test
    fun it_should_delete_note() {
        noteEntity?.let { noteDao?.put(it) }

        var entities = noteDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
         entities?.forEach {
             assertThat(it?.id, `is`(equalTo(1L)))
         }

        noteEntity?.let { noteDao?.remove(it) }

        entities = noteDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(0)))
    }

    @Test
    fun it_should_update_name() {
        val newText = "New Text"
        assertThat(newText, `is`(not(equalTo(noteEntity?.text))))

        noteEntity?.let { noteDao?.put(it) }

        var entities = noteDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        assertThat(entities?.first()?.text, `is`(equalTo(noteEntity?.text)))

        noteEntity?.text = newText
        noteEntity?.let { noteDao?.put(it) }

        entities = noteDao?.all

        assertThat(entities, `is`(instanceOf(MutableList::class.java)))
        assertThat(entities?.size, `is`(equalTo(1)))
        assertThat(entities?.first()?.text, `is`(equalTo(newText)))
    }
}