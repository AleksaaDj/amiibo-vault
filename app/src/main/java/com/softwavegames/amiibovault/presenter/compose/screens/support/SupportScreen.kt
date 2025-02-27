package com.softwavegames.amiibovault.presenter.compose.screens.support

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBackClick: () -> Unit,
    onPurchaseClicked: () -> Unit,
    buyEnabled: Boolean,
    onRateReviewClicked: () -> Unit,
    onSupportClicked: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { Text(text = stringResource(R.string.support)) },
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

    Column(
        modifier = Modifier
            .padding(top = 105.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.support_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.support_text),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            enabled = buyEnabled,
            onClick = {
                onPurchaseClicked()
            },
        )
        {
            Text(text = stringResource(R.string.remove_ads))
        }
        Button(
            modifier = Modifier,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            onClick = {
                onRateReviewClicked()
            },
        )
        {
            Text(text = stringResource(R.string.rate_amiibo_vault))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            modifier = Modifier
                .clickable {
                    onSupportClicked()
                },
            painter = painterResource(id = R.drawable.kofi_button_dark),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}