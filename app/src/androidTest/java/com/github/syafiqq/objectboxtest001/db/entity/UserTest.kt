package com.github.syafiqq.objectboxtest001.db.entity

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.syafiqq.objectboxtest001.MyObjectBox
import com.github.syafiqq.objectboxtest001.entity.User
import io.objectbox.Box
import io.objectbox.BoxStore
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
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
            status = "active",
            parentId = null
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
}