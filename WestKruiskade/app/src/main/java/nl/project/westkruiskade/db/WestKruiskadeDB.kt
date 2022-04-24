package nl.project.westkruiskade.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nl.project.westkruiskade.db.converters.Converters
import nl.project.westkruiskade.model.Exhibit

@Database(
    entities = [Exhibit::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class WestKruiskadeDB : RoomDatabase() {

    abstract val westKruiskadeDao: WestKruiskadeDao

    companion object {
        @Volatile
        private var instance: WestKruiskadeDB? = null
        private const val DATABASE_NAME = "west_kruiskade_db"
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDB(context).also { instance = it }
        }

        private fun createDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WestKruiskadeDB::class.java, DATABASE_NAME
            ).build()
    }
}