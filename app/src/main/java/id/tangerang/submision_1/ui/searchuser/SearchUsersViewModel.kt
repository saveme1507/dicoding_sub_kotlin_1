package id.tangerang.submision_1.ui.searchuser

import android.app.Application
import androidx.lifecycle.*
import id.tangerang.submision_1.R
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.models.UserModel
import id.tangerang.submision_1.utils.SettingPreferences
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class SearchUsersViewModel(application: Application, private val pref: SettingPreferences) : AndroidViewModel(application) {
    private lateinit var apiRequest: ApiRequest
    private var users: MutableLiveData<MutableList<UserModel>> =
        MutableLiveData<MutableList<UserModel>>()

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun setApiRequest(apiRequest: ApiRequest){
        this.apiRequest = apiRequest
    }

    fun getUsers(): LiveData<MutableList<UserModel>> {
        return users
    }

    fun loadUsers(name: String) {
        apiRequest.get(
            "https://api.github.com/search/users?q=$name",
            {
                try {
                    val jsonObject = JSONObject(it)
                    val items = jsonObject.getJSONArray("items")
                    val users = mutableListOf<UserModel>()
                    for (i in 0 until items.length()) {
                        val objects = items.getJSONObject(i)
                        users.add(
                            UserModel(
                                objects.getString("id"),
                                objects.getString("login"),
                                objects.getString("avatar_url"),
                            )
                        )
                    }
                    this.users.postValue(users)

                    if (jsonObject.getInt("total_count") == 0) {
                        apiRequest.showToast("User tidak ditemukan")
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    apiRequest.showToast(getApplication<Application>().getString(R.string.error_parsing_json))
                }
            }
        )
    }
}