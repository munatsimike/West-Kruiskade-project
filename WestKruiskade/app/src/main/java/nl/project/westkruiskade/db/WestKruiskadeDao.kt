package nl.project.westkruiskade.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import nl.project.westkruiskade.model.Exhibit

@Dao
interface WestKruiskadeDao {
    @Transaction
    suspend fun updateLocalDB(exhibits: List<Exhibit>) {
        clearDb()
        updateDB(exhibits)
    }

    @Query("DELETE FROM exhibit ")
    suspend fun clearDb()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDB(exhibits: List<Exhibit>)

    @Query("SELECT * FROM exhibit")
    fun fetchExhibits(): PagingSource<Int, Exhibit>

    @Query("SELECT * from exhibit WHERE id = :exhibitId")
    fun searchExhibitById(exhibitId: String): LiveData<Exhibit>

    fun searchQuery(query: String) = searchDB("$query%")
    @Query("SELECT * FROM exhibit WHERE artistName LIKE :searchQuery")
    fun searchDB(searchQuery: String): PagingSource<Int, Exhibit>
}