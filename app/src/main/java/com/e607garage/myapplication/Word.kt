package com.e607garage.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "word_table")
data class Word(
    @ColumnInfo(name = "word") var word: String
    ,  @PrimaryKey(autoGenerate = true)  @ColumnInfo(name = "_id")  val _id: Int = 0
    ,@ColumnInfo(name = "color" , defaultValue = "0") var color: Int = 0
    ,@ColumnInfo(name = "checked" ,defaultValue = "false" ) var checked: Boolean = false
)