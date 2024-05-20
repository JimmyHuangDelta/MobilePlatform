package com.delta.mobileplatform.web.controller.localStorage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.delta.mobileplatform.web.controller.localStorage.entity.DeltaLocalStorage
import com.delta.mobileplatform.web.controller.localStorage.entity.DomainEntity

@Database(
    entities = [DeltaLocalStorage::class, DomainEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DeltaLocalDB : RoomDatabase() {
    abstract fun localStorageDao(): LocalStorageDao

}