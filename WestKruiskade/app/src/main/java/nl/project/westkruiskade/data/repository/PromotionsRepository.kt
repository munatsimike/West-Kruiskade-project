package nl.project.westkruiskade.data.repository

import nl.project.westkruiskade.db.WestKruiskadeDao
import javax.inject.Inject

class PromotionsRepository @Inject constructor(
    private var westKruiskadeDao: WestKruiskadeDao
) {
    fun fetchPromotions() = westKruiskadeDao.fetchExhibits()
}