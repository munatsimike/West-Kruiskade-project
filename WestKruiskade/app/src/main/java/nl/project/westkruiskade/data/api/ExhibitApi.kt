package nl.project.westkruiskade.data.api

import nl.project.westkruiskade.model.Exhibit
import retrofit2.http.GET

interface ExhibitApi {
    @GET("api/exhibits")
    suspend fun getExhibits(): List<Exhibit>
}