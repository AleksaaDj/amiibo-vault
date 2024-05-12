package com.softwavegames.amiibovault.presenter.compose.screens.nfcreader

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.Amiibo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AmiiboNfcDetailsViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var myTag: Tag? = null

    private val amiiboPageWithIdTailOffset = 22

    private var _amiiboNfc = MutableLiveData<Amiibo?>()
    var amiiboNfc: LiveData<Amiibo?> = _amiiboNfc


    private fun loadAmiiboFromNfc(amiiboTail: String) {
        repository.getAmiiboSpecific(amiiboTail).onEach {
            if (it.isNotEmpty()) {
                _amiiboNfc.value = it[0]
            }
        }.launchIn(viewModelScope)
    }

    fun clearAmiibo() {
        _amiiboNfc.value = null
    }

    /******************************************************************************
     * Read From NFC Tag
     ****************************************************************************/
    @Suppress("DEPRECATION")
    fun readFromIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            myTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            val mifareUltralightTag = MifareUltralight.get(myTag)
            mifareUltralightTag.connect()
            val pageResponse = mifareUltralightTag.readPages(amiiboPageWithIdTailOffset)
            try {
                parsePageResponse(pageResponse)
            } catch (e: SecurityException) {
                Log.e("NfcReader", e.message.toString())
            }
        }

    }

    private fun parsePageResponse(pageResponse: ByteArray) {
        val amiiboTail: String = String.format("%02x", pageResponse[0]) + String.format(
            "%02x",
            pageResponse[1]
        ) + String.format(
            "%02x",
            pageResponse[2]
        ) + String.format("%02x", pageResponse[3])

        loadAmiiboFromNfc(amiiboTail = amiiboTail)
    }
}