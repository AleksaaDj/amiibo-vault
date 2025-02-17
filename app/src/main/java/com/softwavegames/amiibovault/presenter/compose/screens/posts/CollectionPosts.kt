package com.softwavegames.amiibovault.presenter.compose.screens.posts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.ads.LargeBannerAd
import com.softwavegames.amiibovault.model.CollectionPost
import com.softwavegames.amiibovault.presenter.compose.common.buttons.CreatePostButton
import com.softwavegames.amiibovault.presenter.compose.common.items.CollectionPostItem
import com.softwavegames.amiibovault.presenter.compose.screens.search.EmptyScreen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionPosts(
    collectionPostList: List<CollectionPost>?,
    onCreatePostClicked: () -> Unit,
    isPortrait: Boolean,
    onLikeDislikeClicked: (String) -> Unit,
    likedPostsIds: List<String>?,
    listState: LazyListState,
    showBannerAd: Boolean,
) {

    val showProgress = rememberSaveable {
        mutableStateOf(true)
    }
    val showErrorScreen = rememberSaveable {
        mutableStateOf(false)
    }
    TopAppBar(
        modifier = Modifier
            .padding(start = if (isPortrait) 0.dp else 80.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = {
            Text(
                text =
                stringResource(R.string.community_collections)
            )
        },
    )

    Box(
        modifier = Modifier
            .padding(
                top = if (isPortrait) 100.dp else 90.dp,
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = showProgress.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 150.dp),
                color = Color.Red,
            )
            LaunchedEffect(this) {
                delay(25000)
                showProgress.value = false
                showErrorScreen.value = true
            }
        }
        AnimatedVisibility(visible = showErrorScreen.value) {
            EmptyScreen(stringResource(R.string.error_getting_posts), isPortrait)
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(
                    start = if (isPortrait) 0.dp else 110.dp,
                    end = if (isPortrait) 0.dp else 40.dp
                ),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            if (collectionPostList != null) {
                if (collectionPostList.isNotEmpty()) {
                    val sortedList = collectionPostList.sortedByDescending { it.date?.toDate() }
                    items(count = sortedList.size) { postItem ->
                        CollectionPostItem(
                            userName = sortedList[postItem].name,
                            backgroundColor = sortedList[postItem].backgroundColor,
                            avatarId = sortedList[postItem].avatarId,
                            date = sortedList[postItem].date,
                            imageUrl = sortedList[postItem].image,
                            userText = sortedList[postItem].text,
                            isPortrait = isPortrait,
                            postId = sortedList[postItem].postId,
                            likes = sortedList[postItem].likes,
                            onLikeDislikeClicked = { postId ->
                                onLikeDislikeClicked(postId)
                            },
                            likedPostsIds = likedPostsIds
                        )
                        showProgress.value = false
                        showErrorScreen.value = false
                    }
                    if (showBannerAd) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(bottom = 50.dp),
                                contentAlignment = Alignment.BottomCenter,
                            ) {
                                LargeBannerAd(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
        CreatePostButton(onClick = {
            onCreatePostClicked()
        })
    }
}

fun String.toDate(): Date? {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(this)
}