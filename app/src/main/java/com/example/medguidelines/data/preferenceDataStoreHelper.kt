//package com.example.medguidelines.data
//
//import android.content.Context
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.emptyPreferences
//import androidx.datastore.preferences.core.intPreferencesKey
//import androidx.datastore.preferences.core.longPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import java.io.IOException
//
//object PreferenceDataStoreConstants {
//    val IS_MINOR_KEY = booleanPreferencesKey("IS_MINOR_KEY")
//    val AGE_KEY = intPreferencesKey("AGE_KEY")
//    val NAME_KEY = stringPreferencesKey("NAME_KEY")
//    val MOBILE_NUMBER = longPreferencesKey("MOBILE_NUMBER")
//}
//
////interface IPreferenceDataStoreAPI {
////    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T>
////    suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T):T
////    suspend fun <T> putPreference(key: Preferences.Key<T>, value:T)
////    suspend fun <T> removePreference(key: Preferences.Key<T>)
////    suspend fun <T> clearAllPreference()
////
////    companion object {
////        fun putPreference(key: Preferences.Key<String>, value: String) {
////
////        }
////    }
////}
//
//private val Context.dataStore by preferencesDataStore(
//    name = "user_preferences"
//)
//
//class preferenceDataStoreHelper(context: Context) {
//    private val dataSource = context.dataStore
//
//    /* This returns us a flow of data from DataStore.
//Basically as soon we update the value in Datastore,
//the values returned by it also changes. */
//
//    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T):
//            Flow<T> = dataSource.data.catch { exception ->
//        if (exception is IOException){
//            emit(emptyPreferences())
//        }else{
//            throw exception
//        }
//    }.map { preferences->
//        val result = preferences[key]?: defaultValue
//        result
//    }
//
//    /* This returns the last saved value of the key. If we change the value,
//        it wont effect the values produced by this function */
//    suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T) :
//            T = dataSource.data.first()[key] ?: defaultValue
//
//    // This Sets the value based on the value passed in value parameter.
//    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
//        dataSource.edit {preferences ->
//            preferences[key] = value
//        }
//    }
//
//    // This Function removes the Key Value pair from the datastore, hereby removing it completely.
//    suspend fun <T> removePreference(key: Preferences.Key<T>) {
//        dataSource.edit { preferences ->
//            preferences.remove(key)
//        }
//    }
//
//    // This function clears the entire Preference Datastore.
//    suspend fun <T> clearAllPreference() {
//        dataSource.edit { preferences ->
//            preferences.clear()
//        }
//    }
//}
//
