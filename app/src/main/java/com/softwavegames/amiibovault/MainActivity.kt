package com.softwavegames.amiibovault

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.health.connect.datatypes.units.Length
import android.nfc.NfcAdapter
import android.nfc.TagLostException
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.softwavegames.amiibovault.presenter.BottomNavigationBar
import com.softwavegames.amiibovault.presenter.compose.screens.nfcreader.AmiiboNfcDetailsViewModel
import com.softwavegames.amiibovault.ui.theme.AmiiboMvvmComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.InvocationTargetException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var writeTagFilters: Array<IntentFilter>
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private lateinit var navController: NavHostController
    private val viewModel: AmiiboNfcDetailsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.statusBarColor = ContextCompat.getColor(window.context, R.color.black)

        setNFC()
        setNFCIntentListener()

        setContent {

            val bottomBarState = rememberSaveable { mutableStateOf(true) }
            val navigationItemSelectedIndex = rememberSaveable { mutableIntStateOf(0) }
            val isAnimationFinished = rememberSaveable { mutableStateOf(false) }

            AmiiboMvvmComposeTheme {
                LogoAnim {
                    isAnimationFinished.value = true
                }
                navController = rememberNavController()

                DisposableEffect(Unit) {
                    val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
                        when (destination.route) {
                            AppNavigation.BottomNavScreens.AmiiboList.route -> {
                                navigationItemSelectedIndex.intValue = 0
                                bottomBarState.value = true
                            }

                            AppNavigation.BottomNavScreens.AmiiboMyCollection.route -> {
                                navigationItemSelectedIndex.intValue = 1
                                bottomBarState.value = true
                            }

                            else -> {
                                bottomBarState.value = false
                            }
                        }
                    }
                    navController.addOnDestinationChangedListener(callback)
                    onDispose {
                        navController.removeOnDestinationChangedListener(callback)

                    }
                }

                if (isAnimationFinished.value) {
                    BottomNavigationBar(
                        navController = navController,
                        bottomBarState = bottomBarState,
                        navigationSelectedItem = navigationItemSelectedIndex
                    )
                }
            }
            viewModel.amiiboNfc.observe(this) { amiibo ->
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    Constants.PARSED_AMIIBO,
                    amiibo
                )
                navController.navigate(
                    route = AppNavigation.NavigationItem.DetailsScreen.route,
                )
            }
        }
    }

    private fun setNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setNFCIntentListener() {
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        try {
            viewModel.readFromIntent(intent)
        } catch (e: TagLostException) {
            Log.e("NFC read", e.message.toString())
            Toast.makeText(this, "amiibo contact was lost, try again", Toast.LENGTH_LONG).show()

        }
    }

    /**
     * enable foreground dispatch to prevent intent-filter to launch the app again
     */
    private fun enableForegroundDispatch() {
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null)
    }

    /**
     * disable foreground dispatch to allow intent-filter to launch the app
     */
    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    public override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    public override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

}

@Composable
fun LogoAnim(onAnimationFinished: () -> Unit) {
    val alphaValue = remember { Animatable(0f) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.opening),
            contentDescription = "Boats",
            alpha = alphaValue.value,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        LaunchedEffect(key1 = this) {
            alphaValue.animateTo(
                1f,
                animationSpec = tween(1600),
            )
            alphaValue.animateTo(
                0f,
                animationSpec = tween(1500),
            )
            onAnimationFinished()
        }
    }
}


