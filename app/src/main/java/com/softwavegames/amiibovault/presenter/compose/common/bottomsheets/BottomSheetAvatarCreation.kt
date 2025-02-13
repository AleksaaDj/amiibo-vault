package com.softwavegames.amiibovault.presenter.compose.common.bottomsheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.util.getAvatarColorsList
import com.softwavegames.amiibovault.domain.util.getAvatarImagesList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetAvatarCreation(
    onDismiss: () -> Unit,
    onRemoveClicked: () -> Unit,
    onFinishClicked: (backgroundIndex: Int, avatarIndex: Int) -> Unit,
    isPortrait: Boolean
) {
    LocalContext.current

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedColorIndex by remember { mutableIntStateOf(-1) }
    var selectedImageIndex by remember { mutableIntStateOf(-1) }

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        BackgroundSelection(getAvatarColorsList(), onBackgroundSelected = { index ->
            selectedColorIndex = index
        })
        HorizontalDivider(
            modifier = Modifier
                .height(0.5.dp)
                .padding(start = if (isPortrait) 0.dp else 80.dp, top = 10.dp),
            color = Color.LightGray
        )
        AvatarImageSelection(getAvatarImagesList(), onAvatarSelected = { index ->
            selectedImageIndex = index
        })
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 30.dp, end = 30.dp, bottom = 20.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black, contentColor = Color.White
            ),
            onClick = {
                onFinishClicked(selectedColorIndex + 1, selectedImageIndex + 1)
            },
        ) {
            Text(text = stringResource(R.string.finish_creation))
        }
    }
}

@Composable
fun BackgroundSelection(colorsList: List<Color>, onBackgroundSelected: (Int) -> Unit) {
    Column(modifier = Modifier.padding(10.dp)) {
        var selectedColorIndex by remember { mutableIntStateOf(-1) }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 10.dp),
            text = stringResource(R.string.select_background_color),
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(55.dp),
        ) {
            items(colorsList.size) { index ->
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .padding(paddingValues = PaddingValues(5.dp))
                        .clip(CircleShape)
                        .background(colorsList[index])
                        .clickable {
                            selectedColorIndex = index
                            onBackgroundSelected(index)
                        }
                ) {
                    if (selectedColorIndex == index) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_checkmark),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarImageSelection(avatarImagesList: List<Painter>, onAvatarSelected: (Int) -> Unit) {
    var selectedImageIndex by remember { mutableIntStateOf(-1) }

    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 10.dp, top = 15.dp),
            text = stringResource(R.string.select_avatar_image),
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(90.dp),
        ) {
            items(avatarImagesList.size) { index ->
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(paddingValues = PaddingValues(5.dp))
                        .clip(CircleShape)
                        .clickable {
                            selectedImageIndex = index
                            onAvatarSelected(index)
                        }
                ) {
                    Image(
                        painter = avatarImagesList[index],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (selectedImageIndex == index) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_checkmark),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(Color.Black, CircleShape)
                                .padding(3.dp)
                        )
                    }
                }
            }
        }
    }
}

