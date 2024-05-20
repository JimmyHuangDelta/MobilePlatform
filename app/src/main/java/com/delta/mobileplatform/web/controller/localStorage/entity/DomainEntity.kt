package com.delta.mobileplatform.web.controller.localStorage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DomainEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val domain: String
)
