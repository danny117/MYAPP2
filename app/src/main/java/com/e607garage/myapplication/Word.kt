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
    @PrimaryKey() @ColumnInfo(name = "_id") var _id: Int = 0,
    @ColumnInfo(name = "color", defaultValue = "0") var color: Int = 0,
    @ColumnInfo(name = "checked", defaultValue = "false") var checked: Boolean = false,
    @ColumnInfo(name = "recolor", defaultValue = "0") var recolor: Int = 0,
    @ColumnInfo(name = "rechecked", defaultValue = "false") var rechecked: Boolean = false
    ,@ColumnInfo(name = "mode", defaultValue = "0") var mode: Int = 0
    ,@ColumnInfo(name = "remode", defaultValue = "0") var remode: Int = 0
    ,@ColumnInfo(name = "adj1", defaultValue = "0") var adj1: Int = 0
    ,@ColumnInfo(name = "readj1", defaultValue = "0") var readj1: Int = 0
    ,@ColumnInfo(name = "adj2", defaultValue = "0") var adj2: Int = 0
    ,@ColumnInfo(name = "readj2", defaultValue = "0") var readj2: Int = 0


)


