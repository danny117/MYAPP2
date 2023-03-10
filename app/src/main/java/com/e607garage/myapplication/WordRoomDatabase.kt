package com.e607garage.myapplication

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = arrayOf(Word::class, Bluetooth::class)
    , version = 13, exportSchema = true, autoMigrations = [
        AutoMigration(from = 2, to = 3)
        , AutoMigration(from = 3, to = 4)
        , AutoMigration(from = 4, to = 5)
        , AutoMigration(from = 5, to = 6)
        , AutoMigration(from = 6, to = 7)
        , AutoMigration(from = 7, to = 8)
        , AutoMigration(from = 8, to = 9)
        , AutoMigration(from = 9, to = 10)
        , AutoMigration(from = 10, to = 11)
        , AutoMigration(from = 11, to = 12)
        , AutoMigration(from = 12, to = 13)
    ]
)

abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {


                    val wordDao = database.wordDao()

                    //val bluetooth = Bluetooth(true, _id = 0)
                    //wordDao.insert(bluetooth)

                    // Delete all content here.
                    //wordDao.deleteAll()

                    // Add sample words.
                    // TODO: Add your own words!
                    val word = Word("My First Light in the chain")
                    wordDao.insert(word)

                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): WordRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
