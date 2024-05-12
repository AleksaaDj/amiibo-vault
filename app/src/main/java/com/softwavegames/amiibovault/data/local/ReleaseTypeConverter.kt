package com.softwavegames.amiibovault.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.softwavegames.amiibovault.model.Release


@ProvidedTypeConverter
class ReleaseTypeConverter {

    @TypeConverter
    fun releaseToString(release: Release): String {
        return "${release.au},${release.eu},${release.jp},${release.na}"
    }

    @TypeConverter
    fun stringToRelease(release: String): Release {
        return release.split(",").let { releaseArray ->
            Release(releaseArray[0], releaseArray[1], releaseArray[2], releaseArray[3])
        }
    }

}
