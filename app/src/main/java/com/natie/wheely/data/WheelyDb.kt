package com.natie.wheely.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.natie.wheely.model.WheelOption

@Database(entities = [WheelOption::class], version = 1)
abstract class WheelyDb : RoomDatabase() {
    abstract fun optionsDao(): WheelOptionDao

    companion object {
        private var INSTANCE: WheelyDb? = null

        fun getWheelyDb(context: Context?): WheelyDb? {
            if (INSTANCE == null) {
                synchronized(WheelyDb::class) {
                    INSTANCE = context?.applicationContext?.let {
                        Room.databaseBuilder(
                            it,
                            WheelyDb::class.java,
                            "WheelyDb"
                        )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        fun destroySingleton() {
            INSTANCE = null
        }
    }
}

@Dao
interface WheelOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOption(option: WheelOption)

    @Query("SELECT COUNT(id) FROM WheelOption")
    fun getCount(): Int

    @Query("DELETE FROM WheelOption WHERE id = :id")
    fun deleteOption(id: Int)

    @Query("SELECT * FROM WheelOption")
    fun getOptions(): LiveData<List<WheelOption>>
}