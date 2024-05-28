package com.softwavegames.amiibovault

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.nfc.NfcAdapter
import android.nfc.TagLostException
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.softwavegames.amiibovault.presenter.BottomNavigationBar
import com.softwavegames.amiibovault.presenter.compose.common.LogoAnim
import com.softwavegames.amiibovault.presenter.compose.screens.nfcreader.AmiiboNfcDetailsViewModel
import com.softwavegames.amiibovault.ui.theme.AmiiboMvvmComposeTheme
import com.softwavegames.amiibovault.util.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var writeTagFilters: Array<IntentFilter>
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private lateinit var navController: NavHostController
    private val viewModel: AmiiboNfcDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setNFC()
        setNFCIntentListener()

        setContent {

            val bottomBarState = rememberSaveable { mutableStateOf(true) }
            val navigationItemSelectedIndex = rememberSaveable { mutableIntStateOf(0) }
            val isAnimationFinished = rememberSaveable { mutableStateOf(false) }
            var isPortrait by rememberSaveable { mutableStateOf(true) }
            val configuration = LocalConfiguration.current
            isPortrait = when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    false
                }

                else -> {
                    true
                }
            }

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
                                disableForegroundDispatch()
                            }

                            AppNavigation.BottomNavScreens.AmiiboMyCollection.route -> {
                                navigationItemSelectedIndex.intValue = 1
                                bottomBarState.value = true
                                disableForegroundDispatch()
                            }

                            AppNavigation.BottomNavScreens.NfcScanner.route -> {
                                enableForegroundDispatch()
                                bottomBarState.value = false
                            }

                            else -> {
                                bottomBarState.value = false
                                disableForegroundDispatch()
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
                        context = applicationContext,
                        navController = navController,
                        bottomBarState = bottomBarState,
                        navigationSelectedItem = navigationItemSelectedIndex,
                        isPortrait = isPortrait
                    )
                }
            }
            viewModel.amiiboNfc.observe(this) { amiibo ->
                if (amiibo != null) {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        Constants.PARSED_AMIIBO,
                        amiibo
                    )
                    if (navController.currentDestination?.route == AppNavigation.BottomNavScreens.NfcScanner.route) {
                        navController.navigate(
                            route = AppNavigation.NavigationItem.DetailsScreen.route,
                        )
                    }
                }
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
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading amiibo", Toast.LENGTH_LONG).show()
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
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (error: Exception) {
            Log.e(
                "Foreground_Dispatcher",
                "Error stopping foreground dispatch",
                error
            )
        }
    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
        viewModel.clearAmiibo()
    }
}






