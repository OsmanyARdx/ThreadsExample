package edu.farmingdale.threadsexample.countdowntimer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerViewModel : ViewModel() {
    private var timerJob: Job? = null

    // Values selected in time picker
    var selectedHour by mutableIntStateOf(0)
        private set
    var selectedMinute by mutableIntStateOf(0)
        private set
    var selectedSecond by mutableIntStateOf(0)
        private set

    // Total milliseconds when timer starts
    var totalMillis by mutableLongStateOf(0L)
        private set

    // Time that remains
    var remainingMillis by mutableLongStateOf(0L)
        private set

    // Timer's running status
    var isRunning by mutableStateOf(false)
        private set

    var isPaused by mutableStateOf(false)
        private set

    fun selectTime(hour: Int, min: Int, sec: Int) {
        selectedHour = hour
        selectedMinute = min
        selectedSecond = sec
    }

    fun startTimer() {
        totalMillis = (selectedHour * 60 * 60 + selectedMinute * 60 + selectedSecond) * 1000L

        if (totalMillis > 0) {
            isRunning = true
            isPaused = false
            remainingMillis = totalMillis

            timerJob = viewModelScope.launch(Dispatchers.Default) {
                while (remainingMillis > 0 && isRunning) {
                    if (!isPaused) {
                        delay(1000)
                        remainingMillis -= 1000
                        withContext(Dispatchers.Main) {
                        }
                    }
                }

                if (remainingMillis <= 0) {
                    withContext(Dispatchers.Main) {
                        isRunning = false
                    }
                }
            }
        }
    }

    fun pauseOrResumeTimer() {
        if (isRunning) {
            isPaused = !isPaused
        }
    }


    fun cancelTimer() {
        if (isRunning) {
            timerJob?.cancel()
            isRunning = false
            remainingMillis = 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}