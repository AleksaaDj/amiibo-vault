package com.softwavegames.amiibovault.data.remote

import com.softwavegames.amiibovault.model.AmiiboListResponse
import com.softwavegames.amiibovault.model.Games
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AmiiboApi {

    @GET("amiibo/?")
    suspend fun getAmiiboList(@Query("name") name:String): Response<AmiiboListResponse>

    @GET("amiibo/?")
    suspend fun getAmiiboFromSeriesList(@Query("gameseries") gameSeries: String): Response<AmiiboListResponse>

    @GET("amiibo/?&showusage")
    suspend fun getAmiiboConsoles(@Query("tail") tail: String): Response<Games>

    @GET("amiibo/?")
    suspend fun getAmiiboNfc(@Query("tail") tail: String): Response<AmiiboListResponse>
}