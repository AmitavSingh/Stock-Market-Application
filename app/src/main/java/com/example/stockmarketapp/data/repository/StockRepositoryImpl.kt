package com.example.stockmarketapp.data.repository

import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.mapper.toCompanyListing
import com.example.stockmarketapp.data.remote.StockApi
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase
) : StockRepository {

    private val dao = db.dao

    override suspend fun getStockListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            val localListings = dao.searchCompanyListing(query = query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isdbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isdbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache){
                emit(Resource.Loading(isLoading = false))
                return@flow
            }

            val remoteListing = try {
               val response = api.getListings()
            }catch (ex: IOException){
                ex.printStackTrace()
                emit(Resource.Error(message = "Failed to load  data"))
            }catch (ex: HttpException){
                ex.printStackTrace()
                emit(Resource.Error(message = "Failed to load  data"))
            }

        }
    }
}