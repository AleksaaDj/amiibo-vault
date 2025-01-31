package com.softwavegames.amiibovault.presenter.compose.screens.createpost

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.util.rainbowColorsBrush
import com.softwavegames.amiibovault.model.CollectionPost
import com.softwavegames.amiibovault.presenter.compose.common.bottomsheets.BottomSheetAvatarCreation
import com.softwavegames.amiibovault.domain.util.getAvatarColorByIndex
import com.softwavegames.amiibovault.domain.util.getAvatarResourceByIndex
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onBackClick: () -> Unit,
    onPostPublished: () -> Unit,
    isPortrait: Boolean,
    showBannerAd: Boolean,
    onCreatePostClicked: (CollectionPost, bitmap: Bitmap) -> Unit,
    postPublished: Boolean?,
) {
    val context = LocalContext.current

    val showProgress = rememberSaveable {
        mutableStateOf(false)
    }
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var avatarBackgroundSelected by rememberSaveable { mutableIntStateOf(0) }
    var avatarImageSelected by rememberSaveable { mutableIntStateOf(0) }

    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }

    val brushColor = remember {
        rainbowColorsBrush()
    }
    var imageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    if (postPublished == true) {
        onPostPublished()
    }

    LaunchedEffect(imageUri) {
        imageUri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(
                    context.contentResolver, it
                )
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            bitmapState.value = bitmap
        }
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = {
            Text(
                text = stringResource(R.string.create_post),
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
    if (showSheet) {
        BottomSheetAvatarCreation(
            onDismiss = { showSheet = false },
            onFinishClicked = { backgroundIndex, avatarIndex ->
                if (backgroundIndex != 0) {
                    avatarBackgroundSelected = backgroundIndex
                }
                if (avatarIndex != 0) {
                    avatarImageSelected = avatarIndex
                }
                showSheet = false
            },
            isPortrait = isPortrait,
            onRemoveClicked = {
                avatarBackgroundSelected = 0
                avatarImageSelected = 0
            },
        )
    }
    Surface(
        modifier = Modifier.padding(top = 120.dp), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        ) {
            AvatarCreationSection(brushColor = brushColor,
                avatarBackgroundSelected = avatarBackgroundSelected,
                avatarImageSelected = avatarImageSelected,
                showSheet = { showSheet = true })
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                value = nameText,
                onValueChange = { if (it.length <= 20) nameText = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                label = { Text(stringResource(R.string.name_to_be_displayed)) },
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp)
                    .height(100.dp),
                value = descriptionText,
                onValueChange = {
                    if (it.length <= 80) descriptionText = it
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                label = { Text(stringResource(R.string.description_optional)) },
            )

            CollectionImageUpload(onUploadClicked = {
                galleryLauncher.launch("image/*")
            }, bitmapState = bitmapState)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 30.dp, end = 30.dp, bottom = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(visible = showProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 5.dp),
                        color = Color.Red,
                    )
                }
                androidx.compose.animation.AnimatedVisibility(visible = !showProgress.value) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, contentColor = Color.White
                        ),
                        onClick = {
                            val isValidate = when {
                                nameText.isEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.please_fill_your_username),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    false
                                }

                                bitmapState.value == null -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.please_upload_image),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    false
                                }

                                else -> true
                            }

                            if (isValidate) {
                                showProgress.value = true
                                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                val current = LocalDateTime.now(ZoneId.of("UTC")).format(formatter)
                                bitmapState.value?.let { bitmap ->
                                    onCreatePostClicked(
                                        CollectionPost(
                                            postId = (0..1000000).random().toString(),
                                            name = nameText.trim(),
                                            text = descriptionText.trim(),
                                            image = imageUri.toString(),
                                            avatarId = avatarImageSelected,
                                            backgroundColor = avatarBackgroundSelected,
                                            date = current,
                                            likes = "0"
                                        ), bitmap
                                    )
                                }
                            }
                        },
                    ) {
                        Text(text = stringResource(R.string.publish_collection))
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarCreationSection(
    brushColor: Brush,
    avatarBackgroundSelected: Int,
    avatarImageSelected: Int,
    showSheet: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .border(
                    BorderStroke(3.dp, brushColor), CircleShape
                )
                .clip(CircleShape)
                .background(
                    if (avatarBackgroundSelected != 0) getAvatarColorByIndex(
                        avatarBackgroundSelected
                    )
                    else {
                        colorResource(id = R.color.red_avatar)
                    }
                )
                .clickable { showSheet() },
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp), painter = painterResource(
                    id = if (avatarImageSelected != 0) {
                        getAvatarResourceByIndex(
                            avatarImageSelected
                        )
                    } else {
                        R.drawable.ic_avatar_placeholder
                    }
                ), contentDescription = null, contentScale = ContentScale.Fit
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 30.dp, end = 30.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black, contentColor = Color.White
            ),
            onClick = {
                showSheet()
            },
        ) {
            Text(text = stringResource(R.string.create_your_avatar))
        }
    }
}

@Composable
fun CollectionImageUpload(onUploadClicked: () -> Unit, bitmapState: MutableState<Bitmap?>) {
    Box(modifier = Modifier
        .border(
            BorderStroke(1.dp, Color.Gray), RoundedCornerShape(3.dp)
        )
        .fillMaxWidth()
        .height(300.dp)
        .clickable {
            onUploadClicked()
        }) {
        bitmapState.value?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                modifier = Modifier.fillMaxWidth(),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }

        if (bitmapState.value == null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_upload),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(bottom = 10.dp)
                )
                Text(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = stringResource(R.string.upload_image_of_amiibo_collection)
                )
            }
        }
    }
}

