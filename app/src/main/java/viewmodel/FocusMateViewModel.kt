package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.QuoteRepository
import kotlinx.coroutines.launch

class FocusMateViewModel : ViewModel() {
    var focusDuration by mutableIntStateOf(25)
        private set

    var breakDuration by mutableIntStateOf(5)
        private set

    var dailyGoal by mutableIntStateOf(120)
        private set

    var completedMinutes by mutableIntStateOf(0)
        private set

    var remainingSeconds by mutableIntStateOf(focusDuration * 60)
        private set

    var isRunning by mutableStateOf(false)
        private set

    var showMotivation by mutableStateOf(true)
        private set

    var enableReminder by mutableStateOf(true)
        private set

    private val quoteRepository = QuoteRepository()

    var quoteText by mutableStateOf("Loading motivation...")
        private set

    var quoteAuthor by mutableStateOf("")
        private set

    var quoteError by mutableStateOf(false)
        private set

    fun startTimer() {
        isRunning = true
    }

    fun pauseTimer() {
        isRunning = false
    }

    fun resetTimer() {
        isRunning = false
        remainingSeconds = focusDuration * 60
    }

    fun tickTimer() {
        if (isRunning && remainingSeconds > 0) {
            remainingSeconds -= 1
        }

        if (isRunning && remainingSeconds == 0) {
            isRunning = false
            completedMinutes += focusDuration
            remainingSeconds = focusDuration * 60
        }
    }

    fun updateFocusDuration(minutes: Int) {
        focusDuration = minutes
        isRunning = false
        remainingSeconds = minutes * 60
    }

    fun updateBreakDuration(minutes: Int) {
        breakDuration = minutes
    }

    fun updateDailyGoal(minutes: Int) {
        dailyGoal = minutes
    }

    fun updateShowMotivation(show: Boolean) {
        showMotivation = show
    }

    fun updateEnableReminder(enabled: Boolean) {
        enableReminder = enabled
    }
    fun loadMotivationalQuote() {
        viewModelScope.launch {
            quoteError = false
            quoteText = "Loading motivation..."
            quoteAuthor = ""

            val result = quoteRepository.getMotivationalQuote()

            result.onSuccess { quote ->
                quoteText = quote.content
                quoteAuthor = quote.author
            }.onFailure {
                quoteError = true
                quoteText = "Small progress is still progress."
                quoteAuthor = "FocusMate"
            }
        }
    }
}