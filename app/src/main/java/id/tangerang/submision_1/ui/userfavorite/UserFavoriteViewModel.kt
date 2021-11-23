package id.tangerang.submision_1.ui.userfavorite

import android.app.Application
import androidx.lifecycle.*
import id.tangerang.submision_1.database.Favorite
import id.tangerang.submision_1.repository.FavoriteRepository
import id.tangerang.submision_1.utils.SettingPreferences
import kotlinx.coroutines.launch

class UserFavoriteViewModel(application: Application, private val pref: SettingPreferences) :
    AndroidViewModel(application) {
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getUserFavorites(): LiveData<List<Favorite>> {
        return favoriteRepository.getAllFavorites()
    }
}