package com.delta.mobileplatform.web.controller.localStorage.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.delta.mobileplatform.web.controller.utils.string.addSlashes
import com.delta.mobileplatform.model.LocalStorageResponse

@Entity(
    primaryKeys = ["domainId", "key"],
    foreignKeys = [
        ForeignKey(
            entity = DomainEntity::class,
            parentColumns = ["id"],
            childColumns = ["domainId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeltaLocalStorage(
    val domainId: Long,
    val key: String,
    val value: String
) {
    fun toLocalStateResponse(): LocalStorageResponse {
        return LocalStorageResponse(key, addSlashes(value))
    }
}

