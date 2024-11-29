package com.example.proyectoandroid.data

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): ShoppingDatabase {
        return Room.databaseBuilder(app, ShoppingDatabase::class.java, "shopping_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTaskDao(db: ShoppingDatabase): ShoppingItemDao {
        return db.shoppingItemDao()
    }
}