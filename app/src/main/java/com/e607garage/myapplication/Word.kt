package com.e607garage.myapplication

import android.bluetooth.BluetoothAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bluetooth_table")
data class Bluetooth(
    @ColumnInfo(name = "enabled", defaultValue = "false") var enabled: Boolean,
    @ColumnInfo(
        name = "intenabled",
        defaultValue = "0"
    ) var intenabled: Int = BluetoothAdapter.ERROR,
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "_id", defaultValue = "0") val _id: Int = 0
)

@Entity(tableName = "word_table")
data class Word(
    @ColumnInfo(name = "word") var word: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val _id: Int = 0,
    @ColumnInfo(name = "color", defaultValue = "0") var color: Int = 0,
    @ColumnInfo(name = "checked", defaultValue = "false") var checked: Boolean = false,
    @ColumnInfo(name = "recolor", defaultValue = "0") var recolor: Int = 0,
    @ColumnInfo(name = "rechecked", defaultValue = "false") var rechecked: Boolean = false
)

