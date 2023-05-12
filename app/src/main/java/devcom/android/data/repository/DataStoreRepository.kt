package devcom.android.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.first


val Context.dataStore by preferencesDataStore(name = "authy_preferences")


class DataStoreRepository(context: Context) {

    val context1 = context

    suspend fun saveDataToDataStore(data: String, key:String){
        val authKey = stringPreferencesKey(key)
        context1.dataStore.edit { preferences ->
            preferences[authKey] = data
        }
    }

    suspend fun getDataFromDataStore(key:String): String? {
        val authKey = stringPreferencesKey(key)
        val preferences = context1.dataStore.data.first()
        return preferences[authKey]
    }

}