package com.natie.wheely.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WheelOption (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String
)