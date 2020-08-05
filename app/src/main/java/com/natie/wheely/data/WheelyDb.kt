package com.natie.wheely.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.natie.wheely.model.WheelOption

@Database(entities = [WheelOption::class], version = 1)
abstract class WheelyDb : RoomDatabase() {
    abstract fun optionsDao(): WheelOptionDao

    companion object {
        var INSTANCE: WheelyDb? = null

        fun getWheelyDb(context: Context): WheelyDb? {
            if (INSTANCE == null) {
                synchronized(WheelyDb::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        WheelyDb::class.java,
                        "WheelyDb"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}

@Dao
interface WheelOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOption(option: WheelOption)

    @Update
    fun updateOption(option: WheelOption)

    @Delete
    fun deleteOption(option: WheelOption)

    @Query("SELECT * FROM WheelOption WHERE name == :name")
    fun getOptionByName(name: String): List<WheelOption>

    @Query("SELECT * FROM WheelOption")
    fun getOptions(): List<WheelOption>
}