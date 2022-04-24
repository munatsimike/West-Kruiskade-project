package nl.project.westkruiskade.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "exhibit")
data class Exhibit(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val artStyle: String,
    val artistId: String,
    val artistName: String,
    val audios: List<Audio>,
    val images: List<Image>,
    val location: String,
    val name: String,
    val story: String
): Serializable