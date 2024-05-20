package com.delta.mobileplatform.web.controller.localStorage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.delta.mobileplatform.web.controller.localStorage.entity.DeltaLocalStorage
import com.delta.mobileplatform.web.controller.localStorage.entity.DomainEntity

@Dao
interface LocalStorageDao {

    @Transaction
    suspend fun getOrCreateDomainId(domain: String): Long {
        val existingDomain = getDomainId(domain)
        return existingDomain?.id ?: insertDomain(DomainEntity(domain = domain))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomain(domainEntity: DomainEntity): Long

    @Query("SELECT * FROM DomainEntity WHERE domain = :domain")
    suspend fun getDomainId(domain: String): DomainEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(deltaLocalStorage: DeltaLocalStorage)

    @Query("SELECT * FROM DeltaLocalStorage WHERE domainId = :domainId AND `key` = :key")
    suspend fun get(domainId: Long, key: String): DeltaLocalStorage?

    @Query("SELECT * FROM DeltaLocalStorage WHERE domainId = :domainId")
    suspend fun getAll(domainId: Long): List<DeltaLocalStorage>

    @Query("DELETE FROM DeltaLocalStorage WHERE  domainId = :domainId AND `key` = :key")
    suspend fun remove(domainId: Long, key: String)

    @Query("DELETE FROM DeltaLocalStorage WHERE domainId = :domainId")
    suspend fun clear(domainId: Long)
}