package com.antoniogage.projectrc

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert

@Entity(tableName = "Speed")
data class Speed(
    @PrimaryKey val uid: Int = 1,
    @ColumnInfo(name = "motor_speed") val motorSpeed: Int = 0,


)

@Dao
interface SpeedDao {
    @Query("SELECT * FROM Speed WHERE uid = 1")
    suspend fun getSpeed(): Speed?

    @Delete
    suspend fun delete(speed: Speed)

    @Upsert
    suspend fun upsert(speed: Speed)

    @Query("UPDATE Speed SET motor_speed = :speed WHERE uid = 1")
    suspend fun updateSpeed(speed: Int)
}

@Database(entities = [Speed::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun speedDao(): SpeedDao

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

