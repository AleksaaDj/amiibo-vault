package com.softwavegames.amiibovault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboWishlist
import kotlinx.coroutines.flow.Flow


@Dao
interface AmiiboDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMyCollection(amiibo: Amiibo)

    @Delete
    suspend fun deleteFromMyCollection(amiibo: Amiibo)

    @Query("SELECT * FROM Amiibo")
    fun getAmiiboFromMyCollection(): Flow<List<Amiibo>>

    @Query("SELECT * FROM Amiibo WHERE tail =:tail")
    fun getAmiiboSpecificCollection(tail: String): Flow<List<Amiibo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWishlist(amiibo: AmiiboWishlist)

    @Delete
    suspend fun deleteFromWishlist(amiibo: AmiiboWishlist)

    @Query("SELECT * FROM AmiiboWishlist")
    fun getAmiiboFromWishlist(): Flow<List<AmiiboWishlist>>

    @Query("SELECT * FROM AmiiboWishlist WHERE tail =:tail")
    fun getAmiiboSpecificWishlist(tail: String): Flow<List<AmiiboWishlist>>
}