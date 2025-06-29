package com.example.diagnostic333

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dtcs_msg")
data class DtcsMsg(
    @PrimaryKey val name: String,
    val description: String
)