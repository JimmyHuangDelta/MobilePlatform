package com.delta.mobileplatform.web.controller.localStorage

import android.util.Log
import com.delta.mobileplatform.web.controller.localStorage.entity.DeltaLocalStorage
import javax.inject.Inject

class DBRepositoryImpl @Inject constructor(

    val localStorageDao: LocalStorageDao
) : DbRepository {

    private suspend fun getOrCreateDomainId(domain: String): Long {
        return localStorageDao.getOrCreateDomainId(domain)
    }

    override suspend fun set(domain: String, key: String, value: String): DeltaLocalStorage? {
        Log.d("DBRepositoryImpl", "before id")
        val domainId = getOrCreateDomainId(domain)
        Log.d("DBRepositoryImpl", "id $domainId")

        localStorageDao.set(
            DeltaLocalStorage(
                domainId = domainId,
                key = key, value = value
            )
        )
        return localStorageDao.get(domainId, key)
    }

    override suspend fun get(domain: String, key: String): DeltaLocalStorage? {
        val domainId = getOrCreateDomainId(domain)
        return localStorageDao.get(domainId, key)
    }

    override suspend fun getAll(domain: String): List<DeltaLocalStorage> {
        val domainId = getOrCreateDomainId(domain)
        return localStorageDao.getAll(domainId)
    }

    override suspend fun clear(domain: String) {
        val domainId = getOrCreateDomainId(domain)
        localStorageDao.clear(domainId)
    }

    override suspend fun remove(domain: String, key: String) {
        val domainId = getOrCreateDomainId(domain)
        localStorageDao.remove(domainId, key)
    }





}

interface DbRepository {

    suspend fun set(domain: String, key: String, value: String): DeltaLocalStorage?

    suspend fun get(domain: String, key: String): DeltaLocalStorage?
    suspend fun getAll(domain: String): List<DeltaLocalStorage>
    suspend fun clear(domain: String)
    suspend fun remove(domain: String, key: String)


}