package com.softwavegames.amiibovault.util

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NfcA
import android.nfc.tech.TagTechnology
import java.io.IOException

class NTAG215 : TagTechnology {

    private val tagMifare: MifareUltralight?
    private val tagNfcA: NfcA?
    private var maxTransceiveLength: Int = 0

    constructor(nfcA: NfcA?) {
        tagNfcA = nfcA?.also {
            maxTransceiveLength = it.maxTransceiveLength / 4 + 1
        }
        tagMifare = null
    }

    constructor(mifare: MifareUltralight?) {
        tagNfcA = null
        tagMifare = mifare?.also {
            maxTransceiveLength = it.maxTransceiveLength / 4 + 1
        }
    }

    @Suppress("unused")
    var timeout: Int
        get() = tagMifare?.timeout ?: tagNfcA?.timeout ?: 0
        set(timeout) {
            tagMifare?.timeout = timeout
            tagNfcA?.timeout = timeout
        }

    @Throws(IOException::class)
    override fun connect() {
        tagMifare?.connect() ?: tagNfcA?.connect()
    }

    override fun isConnected(): Boolean {
        return tagMifare?.isConnected ?: tagNfcA?.isConnected ?: false
    }

    @Throws(IOException::class)
    override fun close() {
        tagMifare?.close() ?: tagNfcA?.close()
    }

    @Throws(IOException::class)
    fun readPages(pageOffset: Int): ByteArray? {
        return tagMifare?.readPages(pageOffset) ?: if (null != tagNfcA) {
            validatePageIndex(pageOffset)
            val cmd = byteArrayOf(
                NfcByte.CMD_READ.toByte(), pageOffset.toByte()
            )
            tagNfcA.transceive(cmd)
        } else {
            null
        }
    }

    override fun getTag(): Tag? {
        return tagMifare?.tag ?: tagNfcA?.tag
    }
    companion object {

        private infix fun Short.equals(i: Int): Boolean = this == i.toShort()

        private const val NXP_MANUFACTURER_ID = 0x04
        private const val MAX_PAGE_COUNT = 256

        private fun validatePageIndex(pageIndex: Int) {
            // Do not be too strict on upper bounds checking, since some cards
            // may have more addressable memory than they report.
            // Note that issuing a command to an out-of-bounds block is safe - the
            // tag will wrap the read to an addressable area. This validation is a
            // helper to guard against obvious programming mistakes.
            if (pageIndex < 0 || pageIndex >= MAX_PAGE_COUNT)
                throw IndexOutOfBoundsException("page out of bounds: $pageIndex")
        }

        private fun getMifareUltralight(tag: Tag?): NTAG215? {
            return MifareUltralight.get(tag)?.let {
                NTAG215(it)
            }
        }

        private fun getNfcA(tag: Tag?): NTAG215? {
            return NfcA.get(tag)?.let {
                if (it.sak equals 0x00 && it.tag.id[0].toInt() == NXP_MANUFACTURER_ID)
                    NTAG215(it)
                else
                    null
            }
        }

        @Throws(IOException::class)
        operator fun get(tag: Tag?): NTAG215? {
            return try {
                getMifareUltralight(tag)?.apply { connect() }
            } catch (ex: IOException) {
                null
            } ?: getNfcA(tag)?.apply { connect() }
        }
    }
}