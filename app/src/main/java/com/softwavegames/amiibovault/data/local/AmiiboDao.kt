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
    suspend fun upsertAmiiboList(amiibo: List<Amiibo>)

    @Query("SELECT * FROM Amiibo")
    fun getAmiiboFromAmiiboList(): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE name LIKE '%' || :name || '%'")
    fun searchAmiiboList(name: String): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE gameSeries =:gameSeries ")
    fun getAmiiboListFromSeries(gameSeries: String): Flow<List<Amiibo>>
    @Query("SELECT * FROM Amiibo WHERE tail =:tail ")
    fun getAmiiboNFC(tail: String): Flow<List<Amiibo>>

    /**
     * Collection DB
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMyCollection(amiibo: AmiiboCollection)
    @Delete
    suspend fun deleteFromMyCollection(amiibo: AmiiboCollection)
    @Query("SELECT * FROM AmiiboCollection")
    fun getAmiiboFromMyCollection(): Flow<List<AmiiboCollection>>
    @Query("SELECT * FROM AmiiboCollection WHERE tail =:tail")
    fun getAmiiboSpecificCollection(tail: String): Flow<List<AmiiboCollection>>

    /**
     * Wishlist DB
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWishlist(amiibo: AmiiboWishlist)
    @Delete
    suspend fun deleteFromWishlist(amiibo: AmiiboWishlist)
    @Query("SELECT * FROM AmiiboWishlist")
    fun getAmiiboFromWishlist(): Flow<List<AmiiboWishlist>>
    @Query("SELECT * FROM AmiiboWishlist WHERE tail =:tail")
    fun getAmiiboSpecificWishlist(tail: String): Flow<List<AmiiboWishlist>>
}