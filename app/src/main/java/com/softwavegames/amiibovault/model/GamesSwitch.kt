package com.softwavegames.amiibovault.model

data class GamesSwitch(
    val gameID: List<String>,
    val gameName: String,
    val amiiboUsage: List<AmiiboUsage>
)