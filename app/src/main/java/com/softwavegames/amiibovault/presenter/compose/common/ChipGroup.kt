package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.ui.theme.Black
import com.softwavegames.amiibovault.ui.theme.SemiLightGray

@Composable
fun ChipGroup(
    onFilterSelected: (String, String) -> Unit,
    isFiltersSelected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        var seriesFilter by rememberSaveable { mutableStateOf("") }
        var typeFilter by rememberSaveable { mutableStateOf("") }

        ChipSeries {
            seriesFilter = it
            onFilterSelected(seriesFilter, typeFilter)
        }
        ChipType(
            onItemSelected = { typeFilter = it },
            isFiltersSelected = isFiltersSelected
        )
    }
}

@Composable
private fun ChipType(onItemSelected: (String) -> Unit, isFiltersSelected: Boolean) {

    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(false) }
    var selectedItemLabel by rememberSaveable { mutableStateOf("") }

    if(!isFiltersSelected) {
        selected = false
    }
    if (showSheet) {
        BottomSheetType(
            onDismiss = { showSheet = false },
            onItemClick = { selectedItem ->
                selected = true
                selectedItemLabel = selectedItem
                onItemSelected(selectedItemLabel)
            },
            onRemoveClicked = {
                selected = false
                selectedItemLabel = ""
                onItemSelected(selectedItemLabel)
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
            color = if (selected) Black else SemiLightGray
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected) selectedItemLabel else "Type",
                    modifier = Modifier.padding(
                        start = 7.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ),
                    color = if (selected) Color.White else Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = if (selected) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun ChipSeries(onItemSelected: (String) -> Unit) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(false) }
    var selectedItemLabel by rememberSaveable { mutableStateOf("") }


    if (showSheet) {
        BottomSheetSeries(
            onDismiss = { showSheet = false },
            onItemClick = { selectedItem ->
                selected = true
                selectedItemLabel = selectedItem
                onItemSelected(selectedItemLabel)
            },
            onRemoveClicked = {
                selected = false
                selectedItemLabel = ""
                onItemSelected(selectedItemLabel)
            }
        )
    }
    Box(modifier = Modifier
        .padding(end = 5.dp)
        .clickable {
            showSheet = true
        }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (selected) Black else SemiLightGray
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected) selectedItemLabel else "Series",
                    modifier = Modifier.padding(
                        start = 7.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ),
                    color = if (selected) Color.White else Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    tint = if (selected) Color.White else Color.Black
                )
            }
        }
    }
}