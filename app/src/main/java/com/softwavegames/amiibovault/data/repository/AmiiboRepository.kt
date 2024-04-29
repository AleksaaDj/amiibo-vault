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
    suspend fun getAmiiboList(name: String) = amiiboApi.getAmiiboList(name)
    suspend fun getCompatibilityConsoles(tail: String) = amiiboApi.getAmiiboConsoles(tail)

    /**
     * Local
     * Home List DB
     */
    suspend fun upsertAmiiboList(amiibo: List<Amiibo>) = amiiboDao.upsertAmiiboList(amiibo)
    fun selectAmiiboDbList(): Flow<List<Amiibo>> = amiiboDao.getAmiiboFromAmiiboList()
    fun searchAmiiboList(name: String): Flow<List<Amiibo>> =
        amiiboDao.searchAmiiboList(name)
    fun getAmiiboFromSeriesList(gameSeries: String) =
        amiiboDao.getAmiiboListFromSeries(gameSeries)
    fun getAmiiboNfc(tail: String) = amiiboDao.getAmiiboNFC(tail)

    /**
     * My Collection DB
     */
    suspend fun upsertAmiiboMyCollection(amiibo: AmiiboCollection) =
        amiiboDao.upsertMyCollection(amiibo)
    suspend fun deleteAmiiboMyCollection(amiibo: AmiiboCollection) =
        amiiboDao.deleteFromMyCollection(amiibo)
    fun selectAmiiboDbMyCollection(): Flow<List<AmiiboCollection>> =
        amiiboDao.getAmiiboFromMyCollection()
    fun selectAmiiboSpecificDbMyCollection(tail: String): Flow<List<AmiiboCollection>> =
        amiiboDao.getAmiiboSpecificCollection(tail)

    /**
     * Wishlist DB
     */
    suspend fun upsertAmiiboWishlist(amiibo: AmiiboWishlist) = amiiboDao.upsertWishlist(amiibo)
    suspend fun deleteAmiiboWishlist(amiibo: AmiiboWishlist) = amiiboDao.deleteFromWishlist(amiibo)
    fun selectAmiiboDdWishlist(): Flow<List<AmiiboWishlist>> = amiiboDao.getAmiiboFromWishlist()
    fun selectAmiiboSpecificDbWishlist(tail: String): Flow<List<AmiiboWishlist>> =
        amiiboDao.getAmiiboSpecificWishlist(tail)

}