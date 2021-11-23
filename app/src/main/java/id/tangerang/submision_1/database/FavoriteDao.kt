package id.tangerang.submision_1.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: Favorite) : Long

    @Update
    fun update(favorite: Favorite)

    @Delete
    fun delete(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE username = :username")
    fun deleteFavorite(username: String)

    @Query("SELECT * from favorite ORDER BY id DESC")
    fun getAllFavorite(): LiveData<List<Favorite>>

    @Query("SELECT * from favorite WHERE username = :username")
    fun getFavorite(username: String): LiveData<Favorite>
}