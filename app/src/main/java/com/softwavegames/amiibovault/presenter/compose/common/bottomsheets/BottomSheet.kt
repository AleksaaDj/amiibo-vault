package com.softwavegames.amiibovault.presenter.compose.common.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.util.AmiiboFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetType(
    onDismiss: () -> Unit,
    onItemClick: (String) -> Unit,
    onRemoveClicked: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        TypeList(onDismiss, onItemClick, onRemoveClicked)
    }
}

@Composable
fun TypeList(onDismiss: () -> Unit, onClick: (String) -> Unit, onRemoveClicked: () -> Unit) {
    val types = AmiiboFilters.types

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRemoveClicked()
                    onDismiss()
                },
            text = stringResource(R.string.remove_filter),
            color = Color.Red,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 30.dp),
    ) {
        items(types) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onClick(it)
                        },
                    text = it,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSet(
    onDismiss: () -> Unit,
    onItemClick: (String) -> Unit,
    onRemoveClicked: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        SetList(onDismiss, onItemClick, onRemoveClicked)
    }
}

@Composable
fun SetList(onDismiss: () -> Unit, onClick: (String) -> Unit, onRemoveClicked: () -> Unit) {
    val sets = AmiiboFilters.sets

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRemoveClicked()
                    onDismiss()
                },
            text = stringResource(R.string.remove_filter),
            color = Color.Red,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 30.dp),
    ) {
        items(sets) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onClick(it)
                        },
                    text = it,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSort(onDismiss: () -> Unit, onItemClick: (String) -> Unit, onRemoveClicked: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        SortList(onDismiss, onItemClick, onRemoveClicked)
    }
}

@Composable
fun SortList(onDismiss: () -> Unit, onClick: (String) -> Unit, onRemoveClicked: () -> Unit) {
    val sortTypes = AmiiboFilters.sortTypes

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onRemoveClicked()
                    onDismiss()
                },
            text = stringResource(R.string.remove_filter),
            color = Color.Red,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 30.dp),
    ) {
        items(sortTypes) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onClick(it)
                        },
                    text = it,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

