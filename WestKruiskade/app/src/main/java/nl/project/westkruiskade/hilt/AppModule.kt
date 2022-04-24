package nl.project.westkruiskade.hilt

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nl.project.westkruiskade.data.api.ExhibitApi
import nl.project.westkruiskade.data.api.RemoteDataSource
import nl.project.westkruiskade.data.repository.ExhibitRepositoryImp
import nl.project.westkruiskade.db.WestKruiskadeDB
import nl.project.westkruiskade.db.WestKruiskadeDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {
    companion object {
        @Singleton
        @Provides
        fun provideExhibitApi(remoteDataSource: RemoteDataSource) = remoteDataSource.buildApi(
            ExhibitApi::class.java
        )

        @Provides
        fun provideExhibitDao(@ApplicationContext appContext: Context) =
            WestKruiskadeDB.invoke(appContext).westKruiskadeDao

        @Provides
        fun provideExhibitRepository(westKruiskadeDao: WestKruiskadeDao, exhibitApi: ExhibitApi) =
            ExhibitRepositoryImp(westKruiskadeDao, exhibitApi)
    }
}