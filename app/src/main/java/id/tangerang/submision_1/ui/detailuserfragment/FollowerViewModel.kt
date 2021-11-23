package id.tangerang.submision_1.ui.detailuserfragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.tangerang.submision_1.R
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.models.UserModel
import org.json.JSONArray
import org.json.JSONException

class FollowerViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var apiRequest: ApiRequest
    private var users: MutableLiveData<MutableList<UserModel>> =
        MutableLiveData<MutableList<UserModel>>()

    fun setApiRequest(apiRequest: ApiRequest) {
        this.apiRequest = apiRequest
    }

    fun getUsers(): LiveData<MutableList<UserModel>> {
        return users
    }

    fun loadUsers(name: String) {
        apiRequest.get(
            "https://api.github.com/users/$name/followers",
            {
                try {
                    val items = JSONArray(it)
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

                } catch (e: JSONException) {
                    e.printStackTrace()
                    apiRequest.showToast(getApplication<Application>().getString(R.string.error_parsing_json))
                }
            }
        )
    }
}