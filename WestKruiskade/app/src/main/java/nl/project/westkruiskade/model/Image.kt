package nl.project.westkruiskade.model

import java.io.Serializable
@kotlinx.serialization.Serializable
data class Image(
    val createdDate: String,
    val description: String,
    val id: String,
    val imageUri: String,
    val name: String
) : Serializable