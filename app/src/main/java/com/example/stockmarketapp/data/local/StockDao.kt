package com.example.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stockmarketapp.domain.model.CompanyListing

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListing(
        companyListingEntities: List<CompanyListingEntity>
    )

    @Query("Delete from companylistingentity")
    suspend fun clearCompanyListing()

    @Query(
        """
            Select * 
            from companylistingentity
            where LOWER(name) Like '%' || LOWER(:query) || '%' OR
            UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query: String) : List<CompanyListingEntity>
}