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
)

object LabStore {
    private val noteKey = stringPreferencesKey("note")
    private val alarmAtKey = longPreferencesKey("alarm_at")
    private val alarmEnabledKey = booleanPreferencesKey("alarm_enabled")

    fun state(context: Context): Flow<LabState> {
        return context.dataStore.data.map { prefs ->
            LabState(
                note = prefs[noteKey].orEmpty(),
                alarmAt = prefs[alarmAtKey] ?: 0L,
                alarmEnabled = prefs[alarmEnabledKey] ?: false,
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
}
