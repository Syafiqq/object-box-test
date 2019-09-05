package com.github.syafiqq.objectboxtest001.db.session

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.Note
import com.github.syafiqq.objectboxtest001.entity.Note_
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class SessionTest {
    private var context: Context? = null
    private var db: BoxStore? = null
    private var noteDao: Box<Note>? = null
    private var noteEntity: Note? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        noteEntity = Note(
            id = null,
            text = "This is text",
            date = Date()
        )
        assertThat(noteEntity, `is`(notNullValue()))
        assertThat(noteDao, `is`(nullValue()))
        assertThat(db, `is`(nullValue()))
        assertThat(context, `is`(notNullValue()))
    }

    @After
    fun tearDown() {
        noteDao?.removeAll()
        db?.close()
    }

    @Test
    fun it_should_return_different_location_with_same_session() {
        db = MyObjectBox.builder().androidContext(context!!).build()
        noteDao = db?.boxFor(Note::class.java)

        noteEntity?.let { noteDao?.put(it) }

        val query = noteDao?.query()
            ?.equal(Note_.id, noteEntity?.id ?: 0L)
            ?.build()

        val byId1 = query?.find()?.first()
        val locId1 = System.identityHashCode(byId1)

        val byId2 = query?.find()?.first()
        val locId2 = System.identityHashCode(byId2)

        val byId3 = query?.find()?.first()
        val locId3 = System.identityHashCode(byId3)

        assertThat(byId1, `is`(equalTo(byId2)))
        assertThat(byId2, `is`(equalTo(byId3)))
        assertThat(byId3, `is`(equalTo(byId1)))
        assertThat(locId1, `is`(not(equalTo(locId2))))
        assertThat(locId2, `is`(not(equalTo(locId3))))
        assertThat(locId3, `is`(not(equalTo(locId1))))
        byId1?.text = "New Text"
        assertThat(byId1?.text, `is`(not(equalTo(byId2?.text))))
    }
}