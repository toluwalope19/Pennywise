package com.example.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.datastore.PennywiseDataStore
import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pennywise.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Food', 'restaurant', '#FF8A3D', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Shopping', 'shopping_bag', '#FF7AC1', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Health', 'fitness_center', '#5AE9C8', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Transport', 'directions_car', '#4FD1FF', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Education', 'menu_book', '#B79CFF', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Utilities', 'bolt', '#FFD25A', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Travel', 'flight', '#5AE9C8', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Income', 'payments', '#00E5A0', 1)")
                    db.execSQL("INSERT INTO categories (name, icon, color, isDefault) VALUES ('Other', 'more_horiz', '#8C8C8C', 1)")
                }
            })
            .build()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao =
        db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao =
        db.categoryDao()

    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao =
        db.budgetDao()

    @Provides
    @Singleton
    fun providePennywiseDataStore(
        @ApplicationContext context: Context
    ): PennywiseDataStore = PennywiseDataStore(context)
}