package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.hamcrest.CoreMatchers.`is` as were

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun init() {
        fakeDataSource = FakeDataSource()
        fakeDataSource.saveReminders(
            ReminderDTO(
                "title",
                "description",
                "Google complex",
                0.0,
                0.0
            )
        )
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun loadReminders_successful_checkLoadingToastEvent() {
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), were(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), were(false))
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue(), were(notNullValue()))
    }

    @Test
    fun loadReminders_noData_checkShowNoDataEvent() {
        runBlockingTest {
            fakeDataSource.deleteAllReminders()
        }
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showNoData.getOrAwaitValue(), were(notNullValue()))
    }

    @Test
    fun loadReminders_error_checkSnackBarEvent() {
        fakeDataSource.shouldReturnError(true)
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), were(notNullValue()))
    }
}