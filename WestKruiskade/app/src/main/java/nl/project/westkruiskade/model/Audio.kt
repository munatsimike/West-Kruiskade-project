package nl.project.westkruiskade.model

import kotlinx.serialization.Serializable

@Serializable
data class Audio(
    val audioUri: String,
    val createdDate: String,
    val description: String,
    val id: String,
    val name: String
): java.io.Serializable