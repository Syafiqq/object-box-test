package com.github.syafiqq.objectboxtest001.db

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.Note
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstanceTest {

    private var context: Context? = null
    private var boxStore: BoxStore? = null
    private var noteBox: Box<Note>? = null

    @After
    fun tearDown() {
        boxStore?.close()
    }

    @Test
    fun it_should_instantiate_context() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        assertThat(context, `is`(notNullValue()))
    }

    @Test
    fun it_should_instantiate_box_store() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        boxStore = MyObjectBox.builder()
            .androidContext(context)
            .build()
        assertThat(boxStore, `is`(notNullValue()))
    }

    @Test
    fun it_should_instantiate_note_box() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        boxStore = MyObjectBox.builder()
            .androidContext(context)
            .build()
        noteBox = boxStore?.boxFor(Note::class.java)
        assertThat(noteBox, `is`(notNullValue()))
    }
}