package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeAndroidDataSource : ReminderDataSource {

    companion object {
        const val ERROR_MESSAGE = "Error Happened"
    }
    private var shouldReturnError = false
    private val reminders: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error(ERROR_MESSAGE)
        } else {
            Result.Success(reminders.values.toList())
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun shouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun saveReminders(vararg list: ReminderDTO) {
        for (reminder in list) {
            reminders[reminder.id] = reminder
        }
    }

}