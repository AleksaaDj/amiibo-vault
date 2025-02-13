package com.softwavegames.amiibovault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import kotlinx.coroutines.flow.Flow


@Dao
interface AmiiboDao {

    /**
     * Home List DB
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAmiiboHomeList(amiibo: List<Amiibo>)
    @Query("SELECT * FROM Amiibo ORDER BY name ASC")
    fun getAmiiboListFromHome(): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun searchAmiiboHome(name: String): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE gameSeries =:gameSeries ")
    fun getAmiiboListFromSeries(gameSeries: String): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE tail =:tail ")
    fun getAmiiboFromNFC(tail: String): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE featured = 1")
    fun getCurrentFeaturedAmiibo(): Flow<List<Amiibo>>
    @Query("UPDATE Amiibo SET featured = :featured, color = :color WHERE tail =:tail")
    fun setFeaturedAmiibo(featured: Boolean, color: Int, tail: String)
    @Query("UPDATE Amiibo SET featured = 0 WHERE tail =:tail")
    fun removeFeaturedAmiibo(tail: String)

    /**
     * Collection DB
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMyCollection(amiibo: AmiiboCollection)
    @Delete
    suspend fun deleteAmiiboFromMyCollection(amiibo: AmiiboCollection)
    @Query("SELECT * FROM AmiiboCollection")
    fun getAmiiboListFromMyCollection(): Flow<List<AmiiboCollection>>
    @Query("SELECT * FROM AmiiboCollection WHERE tail =:tail")
    fun getAmiiboFromMyCollection(tail: String): Flow<List<AmiiboCollection>>

    /**
     * Wishlist DB
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWishlist(amiibo: AmiiboWishlist)
    @Delete
    suspend fun deleteAmiiboFromWishlist(amiibo: AmiiboWishlist)
    @Query("SELECT * FROM AmiiboWishlist")
    fun getAmiiboListFromWishlist(): Flow<List<AmiiboWishlist>>
    @Query("SELECT * FROM AmiiboWishlist WHERE tail =:tail")
    fun getAmiiboFromWishlist(tail: String): Flow<List<AmiiboWishlist>>
}