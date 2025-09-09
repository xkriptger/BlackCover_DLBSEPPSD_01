package de.xkript.blackcover.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.xkript.blackcover.core.BlackCoverApp
import de.xkript.blackcover.core.util.dataStores.DataStoreBlackCover
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Singleton
    @Provides
    fun provideBlackCoverApp(@ApplicationContext app: Context): BlackCoverApp =
        app as BlackCoverApp
    
    @Singleton
    @Provides
    fun provideDataStoreBlackCover(app: BlackCoverApp): DataStoreBlackCover =
        DataStoreBlackCover(app)
    
}