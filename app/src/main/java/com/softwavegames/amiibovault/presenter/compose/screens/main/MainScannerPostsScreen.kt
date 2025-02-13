package com.softwavegames.amiibovault.presenter.compose.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.ads.LargeBannerAd
import com.softwavegames.amiibovault.model.CollectionPost
import com.softwavegames.amiibovault.presenter.compose.common.items.CollectionPostItem
import com.softwavegames.amiibovault.presenter.compose.common.buttons.CreatePostButton
import com.softwavegames.amiibovault.presenter.compose.common.dialogs.InDevelopmentAlertDialog
import com.softwavegames.amiibovault.presenter.compose.common.TextSwitch
import com.softwavegames.amiibovault.presenter.compose.screens.search.EmptyScreen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScannerPostsScreen(
    onBackClick: () -> Unit,
    isPortrait: Boolean,
    showBannerAd: Boolean,
    onPurchaseScanClicked: () -> Unit,
    buyEnabledScan: Boolean,
    onSelectionChange: () -> Unit,
    collectionPostList: List<CollectionPost>?,
    onCreatePostClicked: () -> Unit,
    onLikeDislikeClicked: (String) -> Unit,
    likedPostsIds: List<String>?,
    listState: LazyListState,
    openInDevelopmentDialog: MutableState<Boolean>,
    onConfirmationClicked: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val showProgress = rememberSaveable {
        mutableStateOf(true)
    }
    val showErrorScreen = rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = {
            Text(
                text = if (selectedTab == 0) {
                    stringResource(R.string.amiibo_reader)
                } else {
                    stringResource(R.string.community_collections)
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.Red,
                )
            }
        },
    )

    Surface(
        modifier = Modifier
            .padding(top = 90.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current
        val items = rememberSaveable {
            listOf(context.getString(R.string.scanner), context.getString(R.string.community))
        }

        var selectedIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        Column {
            TextSwitch(
                modifier = Modifier
                    .padding(
                        top = if (isPortrait) 10.dp else 0.dp,
                        start = if (isPortrait) 10.dp else 110.dp,
                        end = if (isPortrait) 10.dp else 50.dp
                    ),
                selectedIndex = selectedIndex,
                items = items,
                onSelectionChange = {
                    onSelectionChange()
                    selectedIndex = it
                    selectedTab = selectedIndex
                }
            )

            when (selectedTab) {
                0 ->
                    NfcScannerDetails(
                        isPortrait = isPortrait,
                        showBannerAd = showBannerAd,
                        onPurchaseScanClicked = {
                            onPurchaseScanClicked()
                        },
                        buyEnabledScan = buyEnabledScan
                    )

                1 -> collectionPostList?.let {
                    SharedCollections(
                        collectionPostList = collectionPostList,
                        onCreatePostClicked = {
                            onCreatePostClicked()
                        },
                        showProgress = showProgress,
                        showErrorScreen = showErrorScreen,
                        isPortrait = isPortrait,
                        onLikeDislikeClicked = { postId ->
                            onLikeDislikeClicked(postId)
                        },
                        likedPostsIds = likedPostsIds,
                        listState = listState,
                        openInDevelopmentDialog = openInDevelopmentDialog,
                        onConfirmationClicked = {
                            onConfirmationClicked()
                        }
                    )
                }
            }
        }


    }
}

@Composable
private fun NfcScannerDetails(
    isPortrait: Boolean,
    showBannerAd: Boolean,
    onPurchaseScanClicked: () -> Unit,
    buyEnabledScan: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (isPortrait) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.nfc_anim))
            Box {
                LottieAnimation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(360.dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(70.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_nfc_logo),
                    contentDescription = null
                )
            }
        }
        if (buyEnabledScan) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.enable_scanning_message),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Button(
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        onPurchaseScanClicked()
                    },
                )
                {
                    Text(text = stringResource(R.string.enable_scanning_btn))
                }
            }

        } else {
            Text(
                text = stringResource(R.string.tap_amiibo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }
        if (showBannerAd) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
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

@Composable
private fun SharedCollections(
    collectionPostList: List<CollectionPost>?,
    onCreatePostClicked: () -> Unit,
    showProgress: MutableState<Boolean>,
    showErrorScreen: MutableState<Boolean>,
    isPortrait: Boolean,
    onLikeDislikeClicked: (String) -> Unit,
    likedPostsIds: List<String>?,
    listState: LazyListState,
    openInDevelopmentDialog: MutableState<Boolean>,
    onConfirmationClicked: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (openInDevelopmentDialog.value) {
            InDevelopmentAlertDialog(
                onConfirmation = {
                    onConfirmationClicked()
                },
            )
        }
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