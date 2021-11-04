package com.tsai.shakeit.ext

import androidx.fragment.app.Fragment
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.factory.ViewModelFactory

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Fragment.
 */

fun Fragment.getVmFactory(
    product: Product? = null,
    order: Order? = null,
    shopData: Shop? = null,
    shopId: String? = null,
    shopList: Array<Shop> = arrayOf(),
    shopImg: String? = null,
    userId: String? = null
): ViewModelFactory {
    val repository = (requireContext().applicationContext as ShakeItApplication).shakeItRepository
    return ViewModelFactory(product, order, shopData, repository, shopId, shopList, shopImg, userId)
}



