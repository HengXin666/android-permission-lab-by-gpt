package im.hengxin.permissionlab.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("permission_lab")

data class LabState(
    val note: String = "",
    val alarmAt: Long = 0L,
    val alarmEnabled: Boolean = false,
    val persistentNotificationEnabled: Boolean = false,
    val scheduledNotificationAt: Long = 0L,
    val scheduledNotificationEnabled: Boolean = false,
    val lockScreenNotificationAt: Long = 0L,
    val lockScreenNotificationEnabled: Boolean = false,
    val sensorServiceEnabled: Boolean = false,
)

object LabStore {
    private val noteKey = stringPreferencesKey("note")
    private val alarmAtKey = longPreferencesKey("alarm_at")
    private val alarmEnabledKey = booleanPreferencesKey("alarm_enabled")
    private val persistentNotificationEnabledKey = booleanPreferencesKey("persistent_notification_enabled")
    private val scheduledNotificationAtKey = longPreferencesKey("scheduled_notification_at")
    private val scheduledNotificationEnabledKey = booleanPreferencesKey("scheduled_notification_enabled")
    private val lockScreenNotificationAtKey = longPreferencesKey("lock_screen_notification_at")
    private val lockScreenNotificationEnabledKey = booleanPreferencesKey("lock_screen_notification_enabled")
    private val sensorServiceEnabledKey = booleanPreferencesKey("sensor_service_enabled")

    fun state(context: Context): Flow<LabState> {
        return context.dataStore.data.map { prefs ->
            LabState(
                note = prefs[noteKey].orEmpty(),
                alarmAt = prefs[alarmAtKey] ?: 0L,
                alarmEnabled = prefs[alarmEnabledKey] ?: false,
                persistentNotificationEnabled = prefs[persistentNotificationEnabledKey] ?: false,
                scheduledNotificationAt = prefs[scheduledNotificationAtKey] ?: 0L,
                scheduledNotificationEnabled = prefs[scheduledNotificationEnabledKey] ?: false,
                lockScreenNotificationAt = prefs[lockScreenNotificationAtKey] ?: 0L,
                lockScreenNotificationEnabled = prefs[lockScreenNotificationEnabledKey] ?: false,
                sensorServiceEnabled = prefs[sensorServiceEnabledKey] ?: false,
            )
        }
    }

    suspend fun saveNote(context: Context, value: String) {
        context.dataStore.edit { it[noteKey] = value }
    }

    suspend fun saveAlarm(context: Context, alarmAt: Long, enabled: Boolean) {
        context.dataStore.edit {
            it[alarmAtKey] = alarmAt
            it[alarmEnabledKey] = enabled
        }
    }

    suspend fun savePersistentNotification(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[persistentNotificationEnabledKey] = enabled }
    }

    suspend fun saveScheduledNotification(context: Context, at: Long, enabled: Boolean) {
        context.dataStore.edit {
            it[scheduledNotificationAtKey] = at
            it[scheduledNotificationEnabledKey] = enabled
        }
    }

    suspend fun saveLockScreenNotification(context: Context, at: Long, enabled: Boolean) {
        context.dataStore.edit {
            it[lockScreenNotificationAtKey] = at
            it[lockScreenNotificationEnabledKey] = enabled
        }
    }

    suspend fun saveSensorService(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[sensorServiceEnabledKey] = enabled }
    }
}
