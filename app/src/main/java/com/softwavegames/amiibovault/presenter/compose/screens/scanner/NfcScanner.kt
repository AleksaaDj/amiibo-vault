package com.softwavegames.amiibovault.presenter.compose.screens.scanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScanner(
    onBackClick: () -> Unit,
    isPortrait: Boolean,
    showBannerAd: Boolean,
    onPurchaseScanClicked: () -> Unit,
    buyEnabledScan: Boolean,
    listState: LazyListState,
) {

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
                stringResource(R.string.amiibo_reader)
            )
        },
    )

    Surface(
        modifier = Modifier
            .padding(top = 90.dp, start = if (isPortrait) 20.dp else 90.dp, bottom = 5.dp, end = 20.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        NfcScannerDetails(
            isPortrait = isPortrait,
            showBannerAd = showBannerAd,
            onPurchaseScanClicked = {
                onPurchaseScanClicked()
            },
            buyEnabledScan = buyEnabledScan
        )
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