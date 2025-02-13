package com.softwavegames.amiibovault.presenter.compose.common.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.util.rainbowColorsBrush
import com.softwavegames.amiibovault.domain.util.getAvatarColorByIndex
import com.softwavegames.amiibovault.domain.util.getAvatarResourceByIndex

@Composable
fun CollectionPostItem(
    modifier: Modifier = Modifier,
    userName: String?,
    backgroundColor: Int?,
    avatarId: Int?,
    date: String?,
    imageUrl: String?,
    userText: String?,
    isPortrait: Boolean,
    postId: String?,
    likes: String?,
    onLikeDislikeClicked: (String) -> Unit,
    likedPostsIds: List<String>?
) {
    var isLiked = likedPostsIds?.contains(postId)

    val brushColor = remember {
        rainbowColorsBrush()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 0.dp, end = 0.dp, top = 4.dp, bottom = 15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topEnd = 0.dp
        ),
    ) {
        Column {
            UserInformationWithLikes(
                modifier = modifier,
                brushColor = brushColor,
                backgroundColor = backgroundColor,
                avatarId = avatarId,
                userName = userName,
                date = date,
                postId = postId,
                likes = likes?.toInt(),
                onLikeDislikeClicked = {
                    postId?.let { onLikeDislikeClicked(it) }
                    if (isLiked != null) {
                        isLiked = !isLiked!!
                    }
                },
                isLiked = isLiked
            )

            PostImage(
                modifier = modifier,
                imageUrl = imageUrl ?: ""
            )

            if (userText != null) {
                Text(
                    modifier = modifier.padding(start = 15.dp, bottom = 15.dp, end = 15.dp),
                    text = "$userName: $userText",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .height(0.5.dp)
                    .padding(start = if (isPortrait) 0.dp else 80.dp),
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun UserInformationWithLikes(
    modifier: Modifier,
    brushColor: Brush,
    backgroundColor: Int?,
    avatarId: Int?,
    userName: String?,
    date: String?,
    postId: String?,
    likes: Int?,
    onLikeDislikeClicked: () -> Unit,
    isLiked: Boolean?
) {

    val formattedDate = remember { date?.split(" ")?.firstOrNull() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 15.dp, end = 15.dp, top = 6.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .border(
                    BorderStroke(2.dp, brushColor),
                    CircleShape
                )
                .clip(CircleShape)
                .background(
                    if (backgroundColor != null) {
                        getAvatarColorByIndex(backgroundColor)
                    } else {
                        Color.Gray
                    }
                )
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                painter = painterResource(
                    id = if (avatarId != null) {
                        getAvatarResourceByIndex(avatarId)
                    } else {
                        R.drawable.ic_avatar_placeholder
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (userName != null) {
                Text(
                    modifier = modifier.padding(start = 10.dp),
                    text = stringResource(R.string.shared_by, userName),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                modifier = modifier.padding(start = 10.dp),
                text = formattedDate ?: "",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            modifier = Modifier.padding(bottom = 10.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable {
                        onLikeDislikeClicked()
                    }
                    .size(24.dp),
                painter = if (isLiked == true) painterResource(id = R.drawable.ic_liked) else painterResource(
                    id = R.drawable.ic_not_liked
                ), contentDescription = null,
                tint = Color.Red
            )
            if (likes != null) {
                Text(
                    modifier = modifier.padding(start = 4.dp),
                    text = likes.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun PostImage(modifier: Modifier, imageUrl: String) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = RoundedCornerShape(
            topStart = 15.dp,
            bottomStart = 15.dp,
            bottomEnd = 15.dp,
            topEnd = 15.dp
        ),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = ImageRequest.Builder(context).data(imageUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun CollectionPostItemPreview() {
    CollectionPostItem(
        userName = "Mark",
        backgroundColor = 2,
        avatarId = 4,
        date = "31/01/2025 12:44",
        imageUrl = "https://imgur.com/UCdzgVI.jpg",
        userText = "This is a test post",
        isPortrait = true,
        postId = "1",
        likes = "35",
        onLikeDislikeClicked = {},
        likedPostsIds = emptyList()
    )
}


