package de.xkript.blackcover.core.util.dataStores

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.xkript.blackcover.core.BlackCoverApp
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@Suppress("unused")
abstract class DataStoreManager(private val app: BlackCoverApp, dataStoreName: String) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)
    
    fun getString(key: String, default: String? = null): String? {
        return runBlocking { app.dataStore.data.catch { emit(emptyPreferences()) }.map { it[stringPreferencesKey(key)] }.first() ?: default }
    }
    
    fun getStringFlow(key: String, default: String? = null) = flow {
        app.dataStore.data.catch { emit(default) }.map { it[stringPreferencesKey(key)] }.first()
    }
    
    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return runBlocking { app.dataStore.data.catch { emit(emptyPreferences()) }.map { it[booleanPreferencesKey(key)] }.first() ?: default }
    }
    
    fun getBooleanFlow(key: String, default: Boolean = false) = flow {
        app.dataStore.data.catch { emit(default) }.map { it[booleanPreferencesKey(key)] }.first()
    }
    
    fun getInt(key: String, default: Int = 0): Int {
        return runBlocking { app.dataStore.data.catch { emit(emptyPreferences()) }.map { it[intPreferencesKey(key)] }.first() ?: default }
    }
    
    fun getIntFlow(key: String, default: Int = 0) = flow {
        app.dataStore.data.catch { emit(default) }.map { it[intPreferencesKey(key)] }.first()
    }
    
    fun getLong(key: String, default: Long = 0L): Long {
        return runBlocking { app.dataStore.data.catch { emit(emptyPreferences()) }.map { it[longPreferencesKey(key)] }.first() ?: default }
    }
    
    fun getLongFlow(key: String, default: Long = 0L) = flow {
        app.dataStore.data.catch { emit(default) }.map { it[longPreferencesKey(key)] }.first()
    }
    
    fun getFloat(key: String, default: Float = 0F): Float {
        return runBlocking { app.dataStore.data.catch { emit(emptyPreferences()) }.map { it[floatPreferencesKey(key)] }.first() ?: default }
    }
    
    fun getFloatFlow(key: String, default: Float = 0F) = flow {
        app.dataStore.data.catch { emit(default) }.map { it[floatPreferencesKey(key)] }.first()
    }
    
    fun setValue(key: String, value: Any) {
        when (value) {
            is String  -> runBlocking { app.dataStore.edit { it[stringPreferencesKey(key)] = value } }
            is Boolean -> runBlocking { app.dataStore.edit { it[booleanPreferencesKey(key)] = value } }
            is Int     -> runBlocking { app.dataStore.edit { it[intPreferencesKey(key)] = value } }
            is Long    -> runBlocking { app.dataStore.edit { it[longPreferencesKey(key)] = value } }
            is Float   -> runBlocking { app.dataStore.edit { it[floatPreferencesKey(key)] = value } }
        }
    }
    
    fun clear() {
        runBlocking {
            app.dataStore.edit { it.clear() }
        }
    }
    
    fun removeValue(key: String) {
        runBlocking {
            app.dataStore.edit { it.remove(stringPreferencesKey(key)) }
            app.dataStore.edit { it.remove(booleanPreferencesKey(key)) }
            app.dataStore.edit { it.remove(intPreferencesKey(key)) }
            app.dataStore.edit { it.remove(longPreferencesKey(key)) }
            app.dataStore.edit { it.remove(floatPreferencesKey(key)) }
        }
    }
    
}