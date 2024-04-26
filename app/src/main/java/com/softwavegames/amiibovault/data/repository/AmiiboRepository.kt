package com.softwavegames.amiibovault.data.repository

import com.softwavegames.amiibovault.data.local.AmiiboDao
import com.softwavegames.amiibovault.data.remote.AmiiboApi
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboWishlist
import kotlinx.coroutines.flow.Flow

class AmiiboRepository(private val amiiboApi: AmiiboApi, private val amiiboDao: AmiiboDao) {


    // Remote
    suspend fun getAmiiboList(name: String) = amiiboApi.getAmiiboList(name)

    suspend fun getAmiiboFromSeriesList(gameSeries: String) =
        amiiboApi.getAmiiboFromSeriesList(gameSeries)

    suspend fun getCompatibilityConsoles(tail: String) = amiiboApi.getAmiiboConsoles(tail)

    suspend fun getAmiiboNfc(tail: String) = amiiboApi.getAmiiboNfc(tail)


    // Local
    // My Collection DB
    suspend fun upsertAmiiboMyCollection(amiibo: Amiibo) = amiiboDao.upsertMyCollection(amiibo)

    suspend fun deleteAmiiboMyCollection(amiibo: Amiibo) = amiiboDao.deleteFromMyCollection(amiibo)

    fun selectAmiiboDbMyCollection(): Flow<List<Amiibo>> = amiiboDao.getAmiiboFromMyCollection()

    fun selectAmiiboSpecificDbMyCollection(tail: String): Flow<List<Amiibo>> =
        amiiboDao.getAmiiboSpecificCollection(tail)

    // Wishlist DB
    suspend fun upsertAmiiboWishlist(amiibo: AmiiboWishlist) = amiiboDao.upsertWishlist(amiibo)

    suspend fun deleteAmiiboWishlist(amiibo: AmiiboWishlist) = amiiboDao.deleteFromWishlist(amiibo)

    fun selectAmiiboDdWishlist(): Flow<List<AmiiboWishlist>> = amiiboDao.getAmiiboFromWishlist()

    fun selectAmiiboSpecificDbWishlist(tail: String): Flow<List<AmiiboWishlist>> =
        amiiboDao.getAmiiboSpecificWishlist(tail)

}