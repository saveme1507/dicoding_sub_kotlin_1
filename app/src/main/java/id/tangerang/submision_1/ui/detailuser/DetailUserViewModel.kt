package id.tangerang.submision_1.ui.detailuser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.tangerang.submision_1.R
import id.tangerang.submision_1.database.Favorite
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.models.UserModel
import id.tangerang.submision_1.repository.FavoriteRepository
import org.json.JSONException
import org.json.JSONObject

class DetailUserViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var apiRequest: ApiRequest
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(application)
    private var users: MutableLiveData<UserModel> = MutableLiveData<UserModel>()

    fun insertFavorite(favorite: Favorite) {
        favoriteRepository.insert(favorite)
    }

    fun getFavorite(username: String): LiveData<Favorite> = favoriteRepository.getFavorite(username)

    fun deleteFavorite(username: String) {
        favoriteRepository.deleteFavorite(username)
    }

    fun setApiRequest(apiRequest: ApiRequest) {
        this.apiRequest = apiRequest
    }

    fun getUser(): LiveData<UserModel> {
        return users
    }

    fun loadUser(name: String) {
        apiRequest.showProgressDialog()
        apiRequest.get(
            "https://api.github.com/users/$name",
            {
                try {
                    val jsonObject = JSONObject(it)

                    val user = UserModel(
                        jsonObject.getString("id"),
                        jsonObject.getString("login"),
                        jsonObject.getString("avatar_url"),
                        jsonObject.getString("company"),
                        jsonObject.getString("location"),
                        jsonObject.getString("public_repos"),
                        jsonObject.getString("followers"),
                        jsonObject.getString("following"),
                    )
                    this.users.postValue(user)

                    if (jsonObject.has("message")) {
                        apiRequest.showToast(jsonObject.getString("message"))
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    apiRequest.showToast(getApplication<Application>().getString(R.string.error_parsing_json))
                } finally {
                    apiRequest.dismissProgressDialog()
                }
            }
        )
    }
}