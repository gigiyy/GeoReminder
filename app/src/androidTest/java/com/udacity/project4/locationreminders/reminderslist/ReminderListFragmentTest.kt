package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeAndroidDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

    //    TODO: test the navigation of the fragments.
    //    TODO: test the displayed data on the UI.
    //    TODO: add testing for the error messages.
    private lateinit var appContext: Application
    private lateinit var dataSource: FakeAndroidDataSource

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        dataSource = FakeAndroidDataSource()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    dataSource as ReminderDataSource
                )
            }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        runBlocking {
            dataSource.deleteAllReminders()
        }
    }

    @Test
    fun reminders_displayedInUi() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 0.0, 1.0)
        dataSource.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView)).check(matches(hasDescendant(withText("title"))))
        onView(withId(R.id.reminderssRecyclerView)).check(matches(hasDescendant(withText("description"))))
        onView(withId(R.id.reminderssRecyclerView)).check(matches(hasDescendant(withText("location"))))
    }

    @Test
    fun emptyReminders_showNoData() = runBlockingTest {
        launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun errorResult_showSnackBar() = runBlockingTest {
        dataSource.shouldReturnError(true)
        launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            matches(
                withText(
                    FakeAndroidDataSource.ERROR_MESSAGE
                )
            )
        )
    }

    @Test
    fun clickFab_navigateToSaveReminderFragment() = runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

}