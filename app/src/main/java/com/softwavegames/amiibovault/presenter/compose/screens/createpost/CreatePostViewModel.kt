package com.softwavegames.amiibovault.presenter.compose.screens.createpost

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.Constants.IMGUR_BASE_URL
import com.softwavegames.amiibovault.model.CollectionPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


@HiltViewModel
class CreatePostViewModel @Inject constructor(
) :
    ViewModel() {

    private val database: DatabaseReference = Firebase.database.reference

    val postPublished = MutableLiveData<Boolean>()

    fun uploadImage(collectionPost: CollectionPost, image: Bitmap) {
        try {
            val compressedImage = getResizedBitmap(image)
            getBase64Image(compressedImage, complete = { base64Image ->
                viewModelScope.launch(Dispatchers.Default) {

                    try {
                        val url = URL(IMGUR_BASE_URL)

                        val boundary = "Boundary-${System.currentTimeMillis()}"

                        val httpsURLConnection =
                            withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                        httpsURLConnection.setRequestProperty(
                            "Authorization",
                            "Client-ID ${Constants.IMGUR_CLIENT_ID}"
                        )
                        httpsURLConnection.setRequestProperty(
                            "Content-Type",
                            "multipart/form-data; boundary=$boundary"
                        )

                        httpsURLConnection.requestMethod = "POST"
                        httpsURLConnection.doInput = true
                        httpsURLConnection.doOutput = true

                        var body = ""
                        body += "--$boundary\r\n"
                        body += "Content-Disposition:form-data; name=\"image\""
                        body += "\r\n\r\n$base64Image\r\n"
                        body += "--$boundary--\r\n"

                        val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                        withContext(Dispatchers.IO) {
                            outputStreamWriter.write(body)
                            outputStreamWriter.flush()
                        }
                        val response = httpsURLConnection.inputStream.bufferedReader()
                            .use { it.readText() }  // defaults to UTF-8
                        val jsonObject = JSONTokener(response).nextValue() as JSONObject
                        val data = jsonObject.getJSONObject("data")
                        val collectionPostWithImage =
                            collectionPost.copy(image = data.getString("link") + ".jpg")
                        publishPost(collectionPostWithImage)

                    } catch (e: UnknownHostException) {
                        Log.e("CreatePostViewModel", "No internet connection")
                    }
                }
            })
        } catch (e: Exception) {
            postPublished.value = false
        }
    }

    private fun publishPost(collectionPostWithImage: CollectionPost) {
        database.child(Constants.FIREBASE_DB_POSTS).child(collectionPostWithImage.postId.toString())
            .setValue(collectionPostWithImage)
            .addOnSuccessListener {
                postPublished.value = true
            }
            .addOnFailureListener {
                postPublished.value = false
            }
    }

    private fun getBase64Image(image: Bitmap, complete: (String) -> Unit) {
        viewModelScope.launch {
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val b = outputStream.toByteArray()
            complete(Base64.encodeToString(b, Base64.DEFAULT))
        }
    }

    private fun getResizedBitmap(image: Bitmap): Bitmap {
        val maxSize = 500
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}
