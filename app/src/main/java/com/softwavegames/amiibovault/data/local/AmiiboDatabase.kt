package com.softwavegames.amiibovault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist

@Database(entities = [Amiibo::class, AmiiboWishlist::class, AmiiboCollection::class], version = 26)
@TypeConverters(AmiiboTypeConverter::class)
abstract class AmiiboDatabase : RoomDatabase() {

    abstract val amiiboDao: AmiiboDao
}