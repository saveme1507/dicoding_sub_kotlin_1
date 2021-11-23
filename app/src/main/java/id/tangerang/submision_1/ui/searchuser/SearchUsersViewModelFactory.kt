package id.tangerang.submision_1.ui.searchuser

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import id.tangerang.submision_1.utils.SettingPreferences

class SearchUsersViewModelFactory(private val application: Application, private val pref: SettingPreferences) :
    NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchUsersViewModel::class.java)) {
            return SearchUsersViewModel(application, pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}