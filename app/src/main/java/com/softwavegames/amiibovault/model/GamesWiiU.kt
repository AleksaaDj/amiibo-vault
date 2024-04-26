package com.softwavegames.amiibovault.model

data class GamesWiiU(
    val gameID: List<String>,
    val gameName: String,
    val amiiboUsage: List<AmiiboUsage>

)