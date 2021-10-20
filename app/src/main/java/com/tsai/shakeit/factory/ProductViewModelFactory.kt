package com.tsai.shakeit.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.ui.detail.DrinksDetailViewModel

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Factory for all ViewModels which need [Product].
 */
@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val product: Product
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DrinksDetailViewModel::class.java) ->
                    DrinksDetailViewModel(product)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
