package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is` as were

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanup() = database.close()

    @Test
    fun saveReminder_getBackById() = runBlocking {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 1.0)
        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder(reminder.id)
        assertThat(result, instanceOf(Result.Success::class.java))
        result as Result.Success
        assertThat(result.data.title, were(reminder.title))
        assertThat(result.data.description, were(reminder.description))
        assertThat(result.data.location, were(reminder.location))
        assertThat(result.data.latitude, were(reminder.latitude))
        assertThat(result.data.longitude, were(reminder.longitude))
        assertThat(result.data.id, were(reminder.id))
    }

    @Test
    fun getReminders_notFound() = runBlocking {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 1.0, "id1")
        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder("id2")
        assertThat(result, instanceOf(Result.Error::class.java))
        result as Result.Error
        assertThat(result.message, were("Reminder not found!"))
    }
}