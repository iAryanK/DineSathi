package com.aryan.dinesathi.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun orderDao() : OrderDao
}