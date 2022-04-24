package nl.project.westkruiskade.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.project.westkruiskade.model.Audio
import nl.project.westkruiskade.model.Image

class Converters {

    @TypeConverter
    fun fromStringListToString(value: List<Audio>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToStringList(value: String) : List<Audio>{
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromImageListToString(value: List<Image>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToImageList(value: String) : List<Image>{
        return Json.decodeFromString(value)
    }
}
