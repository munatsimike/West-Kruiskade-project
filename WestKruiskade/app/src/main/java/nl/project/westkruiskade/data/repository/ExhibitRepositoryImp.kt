package nl.project.westkruiskade.data.repository

import androidx.paging.PagingSource
import nl.project.westkruiskade.data.api.ExhibitApi
import nl.project.westkruiskade.db.WestKruiskadeDao
import nl.project.westkruiskade.model.Exhibit
import javax.inject.Inject

class ExhibitRepositoryImp @Inject constructor(
    private val westKruiskadeDao: WestKruiskadeDao,
    private val exhibitApi: ExhibitApi
) : BaseRepository() {

    suspend fun fetchExhibitsFromApi() = safeApiCall {
        return@safeApiCall exhibitApi.getExhibits()
    }

    fun fetchExhibitsFromLocalDB(): PagingSource<Int, Exhibit> {
        return westKruiskadeDao.fetchExhibits()
    }

    suspend fun updateLocalDB(exhibits: List<Exhibit>) {
        westKruiskadeDao.updateLocalDB(exhibits)
    }
}
