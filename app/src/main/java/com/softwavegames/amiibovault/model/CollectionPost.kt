package com.softwavegames.amiibovault.model

data class CollectionPost(
    val postId: String? = "",
    val likes: String? = "0",
    val avatarId: Int? = 0,
    val backgroundColor: Int? = 0,
    val date: String? = "",
    val image: String? = "",
    val name: String? = "",
    val text: String? = "",
)