package com.example.stockmarketapp.presentation.company_list

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    var state by mutableStateOf(CompanyListingState())
    private var searchJob: Job? = null

    init {
        getCompanyListing()
    }

    fun onEvent(event: CompanyListingsEvent) {
        when (event) {
            is CompanyListingsEvent.Refresh -> {
                getCompanyListing(fetchFromRemote = true)
            }

            is CompanyListingsEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListing()
                }
            }
        }
    }

    private fun getCompanyListing(
        fetchFromRemote: Boolean = false,
        query: String = state.searchQuery.lowercase()
    ) {
        viewModelScope.launch {
            repository.getStockListing(fetchFromRemote = fetchFromRemote, query = query)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                        is Resource.Error -> {
                            Log.d("Error", result.message.toString())
                        }
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                state = state.copy(
                                    companies = listings
                                )
                            }
                        }
                    }
                }
        }
    }
}