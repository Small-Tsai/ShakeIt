package com.tsai.shakeit.ext

import androidx.fragment.app.Fragment
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.factory.DetailViewModelFactory

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Fragment.
 */

fun Fragment.getVmFactory(product: Product): DetailViewModelFactory {
    return DetailViewModelFactory(product)
}
