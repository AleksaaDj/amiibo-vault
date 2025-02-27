package com.softwavegames.amiibovault.domain.util

object AmiiboFilters {
    val sets = listOf(
        "8-bit Mario",
        "Animal Crossing",
        "BoxBoy!",
        "Chibi-Robo",
        "Diablo",
        "Fire Emblem",
        "Kirby",
        "Legend Of Zelda",
        "Mario Sports Superstars",
        "Mega Man",
        "Metroid",
        "Monster Hunter",
        "Monster Hunter Rise",
        "Pikmin",
        "Pokemon",
        "Power Pros",
        "Shovel Knight",
        "Skylanders",
        "Splatoon",
        "Super Mario Bros.",
        "Super Nintendo World",
        "Super Smash Bros.",
        "Xenoblade Chronicles 3",
        "Yoshi's Woolly World",
        "Yu-Gi-Oh!",
        "Others"
    )

    val types = listOf(
        "Figure", "Card", "Yarn", "Band"
    )

    val sortTypes = listOf(
        Constants.SORT_TYPE_NAME_ASC,
        Constants.SORT_TYPE_NAME_DSC,
        Constants.SORT_TYPE_RELEASE_ASC,
        Constants.SORT_TYPE_RELEASE_DSC,
        Constants.SORT_TYPE_SET_ASC,
        Constants.SORT_TYPE_SET_DSC
    )
}