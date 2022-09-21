package com.example.stockmarketapp.domain.repository

import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getStockListing(
        fetchFromRemote : Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>
}