package net.pubnative.hybidstandalonedemo.interfaces

import androidx.room.*
import net.pubnative.hybidstandalonedemo.models.History

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT :limit")
    fun getHistory(limit: Int = 10): List<History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(history: History): Long

    @Query("DELETE FROM history")
    fun deleteHistory()
}
