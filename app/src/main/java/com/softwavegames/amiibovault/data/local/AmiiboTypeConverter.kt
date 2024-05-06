package com.softwavegames.amiibovault.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.softwavegames.amiibovault.model.Release


@ProvidedTypeConverter
class AmiiboTypeConverter {

    @TypeConverter
    fun releaseToString(release: Release): String {
        return "${""}${""}${release.jp}${""}"
    }

    @TypeConverter
    fun stringToRelease(release: String): Release {
        return release.split(",").let { releaseArray ->
            Release(releaseArray[0], releaseArray[0], releaseArray[0], releaseArray[0])
        }
    }
}