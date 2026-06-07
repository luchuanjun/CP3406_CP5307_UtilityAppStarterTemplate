package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    CP3406_CP5603UtilityAppStarterTemplateTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Utility") }

    var focusDuration by remember { mutableIntStateOf(25) }
    var breakDuration by remember { mutableIntStateOf(5) }
    var dailyGoal by remember { mutableIntStateOf(120) }
    var completedMinutes by remember { mutableIntStateOf(0) }

    var remainingSeconds by remember { mutableIntStateOf(focusDuration * 60) }
    var isRunning by remember { mutableStateOf(false) }

    var showMotivation by remember { mutableStateOf(true) }
    var enableReminder by remember { mutableStateOf(true) }

    LaunchedEffect(focusDuration) {
        if (!isRunning) {
            remainingSeconds = focusDuration * 60
        }
    }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }

        if (isRunning && remainingSeconds == 0) {
            isRunning = false
            completedMinutes += focusDuration
            remainingSeconds = focusDuration * 60
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(
                    focusDuration = focusDuration,
                    breakDuration = breakDuration,
                    dailyGoal = dailyGoal,
                    completedMinutes = completedMinutes,
                    remainingSeconds = remainingSeconds,
                    isRunning = isRunning,
                    showMotivation = showMotivation,
                    enableReminder = enableReminder,
                    onStart = { isRunning = true },
                    onPause = { isRunning = false },
                    onReset = {
                        isRunning = false
                        remainingSeconds = focusDuration * 60
                    }
                )

                "Settings" -> SettingsScreen(
                    focusDuration = focusDuration,
                    breakDuration = breakDuration,
                    dailyGoal = dailyGoal,
                    showMotivation = showMotivation,
                    enableReminder = enableReminder,
                    onFocusDurationChange = {
                        focusDuration = it
                        isRunning = false
                        remainingSeconds = it * 60
                    },
                    onBreakDurationChange = {
                        breakDuration = it
                    },
                    onDailyGoalChange = {
                        dailyGoal = it
                    },
                    onShowMotivationChange = {
                        showMotivation = it
                    },
                    onEnableReminderChange = {
                        enableReminder = it
                    }
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(
    focusDuration: Int,
    breakDuration: Int,
    dailyGoal: Int,
    completedMinutes: Int,
    remainingSeconds: Int,
    isRunning: Boolean,
    showMotivation: Boolean,
    enableReminder: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    val progress = if (dailyGoal > 0) {
        (completedMinutes.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val progressPercent = (progress * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderSection()

        TimerCard(
            remainingSeconds = remainingSeconds,
            isRunning = isRunning,
            focusDuration = focusDuration
        )

        ProgressCard(
            dailyGoal = dailyGoal,
            completedMinutes = completedMinutes,
            progress = progress,
            progressPercent = progressPercent
        )

        SessionInfoCard(
            focusDuration = focusDuration,
            breakDuration = breakDuration,
            enableReminder = enableReminder
        )

        if (showMotivation) {
            MotivationCard()
        }

        TimerControlButtons(
            isRunning = isRunning,
            onStart = onStart,
            onPause = onPause,
            onReset = onReset
        )
    }
}

@Composable
fun HeaderSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "FocusMate",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Smart Study Timer Utility App",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TimerCard(
    remainingSeconds: Int,
    isRunning: Boolean,
    focusDuration: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = formatTime(remainingSeconds),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = if (isRunning) "Focus session in progress" else "Ready for a $focusDuration-minute focus session",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ProgressCard(
    dailyGoal: Int,
    completedMinutes: Int,
    progress: Float,
    progressPercent: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Study Progress",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Daily goal: $dailyGoal minutes",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Completed: $completedMinutes minutes",
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Progress: $progressPercent%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SessionInfoCard(
    focusDuration: Int,
    breakDuration: Int,
    enableReminder: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Current Session Settings",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Focus duration: $focusDuration minutes",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Break duration: $breakDuration minutes",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (enableReminder) {
                    "Reminder: enabled"
                } else {
                    "Reminder: disabled"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MotivationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Daily Motivation",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = "\"Small progress is still progress.\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun TimerControlButtons(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onStart,
            enabled = !isRunning,
            modifier = Modifier.weight(1f)
        ) {
            Text("Start")
        }

        OutlinedButton(
            onClick = onPause,
            enabled = isRunning,
            modifier = Modifier.weight(1f)
        ) {
            Text("Pause")
        }

        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f)
        ) {
            Text("Reset")
        }
    }
}

@Composable
fun SettingsScreen(
    focusDuration: Int,
    breakDuration: Int,
    dailyGoal: Int,
    showMotivation: Boolean,
    enableReminder: Boolean,
    onFocusDurationChange: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
    onDailyGoalChange: (Int) -> Unit,
    onShowMotivationChange: (Boolean) -> Unit,
    onEnableReminderChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Customise how FocusMate supports your study sessions.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        DurationSettingCard(
            title = "Focus Duration",
            description = "Controls the countdown timer on the main screen.",
            value = focusDuration,
            valueRange = 15f..60f,
            label = "$focusDuration minutes",
            onValueChange = onFocusDurationChange
        )

        DurationSettingCard(
            title = "Break Duration",
            description = "Sets the recommended break time after each focus session.",
            value = breakDuration,
            valueRange = 5f..20f,
            label = "$breakDuration minutes",
            onValueChange = onBreakDurationChange
        )

        DurationSettingCard(
            title = "Daily Study Goal",
            description = "Controls the progress target shown on the main screen.",
            value = dailyGoal,
            valueRange = 60f..240f,
            label = "$dailyGoal minutes",
            onValueChange = onDailyGoalChange
        )

        SwitchSettingCard(
            title = "Show Motivation",
            description = "Display a motivational study message on the utility screen.",
            checked = showMotivation,
            onCheckedChange = onShowMotivationChange
        )

        SwitchSettingCard(
            title = "Enable Reminder",
            description = "Show whether reminders are enabled for completed sessions.",
            checked = enableReminder,
            onCheckedChange = onEnableReminderChange
        )
    }
}

@Composable
fun DurationSettingCard(
    title: String,
    description: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    onValueChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = valueRange
            )
        }
    }
}

@Composable
fun SwitchSettingCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}