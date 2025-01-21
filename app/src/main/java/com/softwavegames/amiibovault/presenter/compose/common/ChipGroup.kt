package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.ui.theme.Black

@Composable
fun ChipGroup(
    onFilterSetSelected: (String) -> Unit,
    onFilterSetRemoved: () -> Unit,
    onFilterTypeSelected: (String) -> Unit,
    onFilterTypeRemoved: () -> Unit,
    onSortTypeSelected: (String) -> Unit,
    onSortTypeRemoved: () -> Unit,
    isPortrait: Boolean
) {
    Row(
        modifier = Modifier
            .padding(start = if (isPortrait) 13.dp else 95.dp, top = 5.dp, end = 7.dp, bottom = 7.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        ChipType(
            onItemSelected = { onFilterTypeSelected(it) },
            onFilterRemoved = onFilterTypeRemoved
        )

        ChipSet(
            onItemSelected = onFilterSetSelected,
            onFilterRemoved = onFilterSetRemoved
        )

        ChipSortType(
            onItemSelected = onSortTypeSelected,
            onFilterRemoved = onSortTypeRemoved
        )
    }
}

@Composable
private fun ChipType(onItemSelected: (String) -> Unit, onFilterRemoved: () -> Unit) {

    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(false) }
    var selectedItemLabel by rememberSaveable { mutableStateOf("") }

    if (showSheet) {
        BottomSheetType(
            onDismiss = { showSheet = false },
            onItemClick = { selectedItem ->
                selected = true
                selectedItemLabel = selectedItem
                onItemSelected(selectedItem)
            },
            onRemoveClicked = {
                onFilterRemoved()
                selected = false
                selectedItemLabel = ""
            }
        )
    }
    Box(modifier = Modifier
        .padding(end = 3.dp)
        .clickable {
            showSheet = true
        }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (selected) Black else MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected) selectedItemLabel else stringResource(id = R.string.type),
                    modifier = Modifier.padding(
                        start = 7.dp,
                        top = 3.dp,
                        bottom = 3.dp
                    ),
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ChipSet(onItemSelected: (String) -> Unit, onFilterRemoved: () -> Unit) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(false) }
    var selectedItemLabel by rememberSaveable { mutableStateOf("") }

    if (showSheet) {
        BottomSheetSet(
            onDismiss = { showSheet = false },
            onItemClick = { selectedItem ->
                selected = true
                selectedItemLabel = selectedItem
                onItemSelected(selectedItemLabel)
            },
            onRemoveClicked = {
                onFilterRemoved()
                selected = false
                selectedItemLabel = ""
            }
        )
    }
    Box(modifier = Modifier
        .padding(end = 3.dp)
        .clickable {
            showSheet = true
        }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (selected) Black else MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected) selectedItemLabel else stringResource(id = R.string.set),
                    modifier = Modifier.padding(
                        start = 7.dp,
                        top = 3.dp,
                        bottom = 3.dp
                    ),
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
@Composable
private fun ChipSortType(onItemSelected: (String) -> Unit, onFilterRemoved: () -> Unit) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(false) }
    var selectedItemLabel by rememberSaveable { mutableStateOf("") }

    if (showSheet) {
        BottomSheetSort(
            onDismiss = { showSheet = false },
            onItemClick = { selectedItem ->
                selected = true
                selectedItemLabel = selectedItem
                onItemSelected(selectedItem)
            },
            onRemoveClicked = {
                onFilterRemoved()
                selected = false
                selectedItemLabel = ""
            }
        )
    }
    Box(modifier = Modifier
        .padding(end = 7.dp)
        .clickable {
            showSheet = true
        }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (selected) Black else MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected) selectedItemLabel else stringResource(R.string.sort),
                    modifier = Modifier.padding(
                        start = 7.dp,
                        top = 3.dp,
                        bottom = 3.dp
                    ),
                    maxLines = 1,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}