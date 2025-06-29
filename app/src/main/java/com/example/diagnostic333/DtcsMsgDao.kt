package com.example.diagnostic333

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DtcsMsgDao {
    @Insert
    suspend fun insert(dtcsMsg: DtcsMsg)

    @Insert
    suspend fun insertAll(dtcsMsgs: List<DtcsMsg>)

    @Query("SELECT * FROM dtcs_msg")
    fun getAll(): Flow<List<DtcsMsg>>

    @Query("SELECT * FROM dtcs_msg WHERE name = :name")
    suspend fun getByName(name: String): DtcsMsg?

    @Query("DELETE FROM dtcs_msg")
    suspend fun deleteAll()
}