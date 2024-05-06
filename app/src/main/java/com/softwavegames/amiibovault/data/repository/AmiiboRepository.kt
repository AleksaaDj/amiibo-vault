package com.softwavegames.amiibovault.data.repository

import com.softwavegames.amiibovault.data.local.AmiiboDao
import com.softwavegames.amiibovault.data.remote.AmiiboApi
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import kotlinx.coroutines.flow.Flow

class AmiiboRepository(private val amiiboApi: AmiiboApi, private val amiiboDao: AmiiboDao) {

    /**
     * Remote
     */
    suspend fun getAmiiboList() =
        amiiboApi.getAmiiboList()

    suspend fun getCompatibilityConsoles(tail: String) =
        amiiboApi.getAmiiboConsoles(tail)

    /**
     * Local
     * Home List DB
     */
    suspend fun upsertAmiiboDbHomeList(amiibo: List<Amiibo>) =
        amiiboDao.upsertAmiiboHomeList(amiibo)

    fun getAmiiboListFromDbHome(): Flow<List<Amiibo>> =
        amiiboDao.getAmiiboListFromHome()

    fun searchAmiiboHome(name: String): Flow<List<Amiibo>> =
        amiiboDao.searchAmiiboHome(name)

    fun getAmiiboListFromSeries(gameSeries: String) =
        amiiboDao.getAmiiboListFromSeries(gameSeries)

    fun getAmiiboFromNfc(tail: String) =
        amiiboDao.getAmiiboFromNFC(tail)

    /**
     * My Collection DB
     */
    suspend fun upsertAmiiboDbMyCollection(amiibo: AmiiboCollection) =
        amiiboDao.upsertMyCollection(amiibo)

    suspend fun deleteAmiiboFromDbMyCollection(amiibo: AmiiboCollection) =
        amiiboDao.deleteAmiiboFromMyCollection(amiibo)

    fun getAmiiboListFromDbMyCollection(): Flow<List<AmiiboCollection>> =
        amiiboDao.getAmiiboListFromMyCollection()

    fun getAmiiboFromDbMyCollection(tail: String): Flow<List<AmiiboCollection>> =
        amiiboDao.getAmiiboFromMyCollection(tail)

    /**
     * Wishlist DB
     */
    suspend fun upsertAmiiboDbWishlist(amiibo: AmiiboWishlist) =
        amiiboDao.upsertWishlist(amiibo)

    suspend fun deleteAmiiboFromDbWishlist(amiibo: AmiiboWishlist) =
        amiiboDao.deleteAmiiboFromWishlist(amiibo)

    fun getAmiiboListFromDdWishlist(): Flow<List<AmiiboWishlist>> =
        amiiboDao.getAmiiboListFromWishlist()

    fun getAmiiboFromDbWishlist(tail: String): Flow<List<AmiiboWishlist>> =
        amiiboDao.getAmiiboFromWishlist(tail)

}