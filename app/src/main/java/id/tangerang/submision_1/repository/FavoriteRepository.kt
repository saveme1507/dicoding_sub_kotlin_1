package id.tangerang.submision_1.repository

import android.app.Application
import androidx.lifecycle.LiveData
import id.tangerang.submision_1.database.Favorite
import id.tangerang.submision_1.database.FavoriteDao
import id.tangerang.submision_1.database.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val favoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        favoriteDao = db.favoriteDao()
    }

    fun getAllFavorites(): LiveData<List<Favorite>> = favoriteDao.getAllFavorite()

    fun getFavorite(username: String): LiveData<Favorite> = favoriteDao.getFavorite(username)

    fun insert(favorite: Favorite) {
        executorService.execute { favoriteDao.insert(favorite) }
    }

    fun deleteFavorite(username: String) {
        executorService.execute { favoriteDao.deleteFavorite(username) }
    }
}
