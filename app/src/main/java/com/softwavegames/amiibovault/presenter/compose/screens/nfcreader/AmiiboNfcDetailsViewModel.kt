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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmiiboNfcDetailsViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var myTag: Tag? = null

    private var _amiiboNfc = MutableLiveData<Amiibo?>()
    var amiiboNfc: LiveData<Amiibo?> = _amiiboNfc

    private fun loadAmiiboConsoleInfo(amiiboTail: String) {
        viewModelScope.launch {
            val amiiboListResponse = repository.getAmiiboNfc(amiiboTail)

            when (amiiboListResponse.isSuccessful) {
                true -> {
                    with(amiiboListResponse.body()) {
                        val amiiboList = this?.amiibo
                        _amiiboNfc.postValue(amiiboList?.get(0))
                    }
                }

                else -> {
                    Log.e("ErrorAmiiboConsoles", amiiboListResponse.message())
                }
            }
        }
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
            for (j in 0..40 step 4) {
                val resp = mifareUltralightTag.readPages(j)
                parseResponse(j, resp)
            }
        }
    }

    private fun parseResponse(j: Int, resp: ByteArray) {
        var i = 0
        var amiiboTail = ""
        while (i < resp.size) {
            if ((j + i / 4) == 22) {
                amiiboTail = String.format("%02x", resp[i]) + String.format(
                    "%02x",
                    resp[i + 1]
                ) + String.format(
                    "%02x",
                    resp[i + 2]
                ) + String.format("%02x", resp[i + 3])
            }
            i += 4
        }
        loadAmiiboConsoleInfo(amiiboTail = amiiboTail)
    }
}