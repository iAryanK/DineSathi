package com.aryan.dinesathi.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("orders")
data class OrderEntity (
    @PrimaryKey val resId: String,
    @ColumnInfo("food_Items") val foodItems : String
        )