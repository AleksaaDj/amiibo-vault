package com.softwavegames.amiibovault.model

data class Games3DS(
    val gameID: List<String>,
    val gameName: String,
    val amiiboUsage: List<AmiiboUsage>

)