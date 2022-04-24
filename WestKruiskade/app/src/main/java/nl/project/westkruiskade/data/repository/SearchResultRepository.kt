package nl.project.westkruiskade.data.repository

import nl.project.westkruiskade.db.WestKruiskadeDao
import javax.inject.Inject

class SearchResultRepository @Inject constructor(
    private val westKruiskadeDao: WestKruiskadeDao,
) {
    fun fetchSearchResultsFromLocalDB(searchQuery: String) =
        westKruiskadeDao.searchQuery(searchQuery)

    fun fetchExhibitById(id: String) = westKruiskadeDao.searchExhibitById(id)
}