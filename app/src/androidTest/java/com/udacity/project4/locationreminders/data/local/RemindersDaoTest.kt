package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is` as were

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun cleanupDb() {
        database.close()
    }

    @Test
    fun saveReminder_getBackById() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 1.0)
        database.reminderDao().saveReminder(reminder)

        val result = database.reminderDao().getReminderById(reminder.id)
        assertThat(result as ReminderDTO, notNullValue())
        assertThat(result.id, were(reminder.id))
        assertThat(result.title, were(reminder.title))
        assertThat(result.description, were(reminder.description))
        assertThat(result.latitude, were(reminder.latitude))
        assertThat(reminder.longitude, were(reminder.longitude))
    }

}