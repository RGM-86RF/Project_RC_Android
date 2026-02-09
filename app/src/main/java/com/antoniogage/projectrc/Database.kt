package com.antoniogage.projectrc

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "Connection")
data class Connections(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "Connection_Type") val connectionType: String,
    @ColumnInfo(name = "Date/Time") val dateTime: Long

)

@Dao
interface ConnectionDao {
    @Query("SELECT * FROM Connection")
    suspend fun getAll(): List<Connections>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg connections: Connections)

    @Delete
    suspend fun delete(connection: Connections)

}

@Database(entities = [Connections::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun connectionDao(): ConnectionDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}

