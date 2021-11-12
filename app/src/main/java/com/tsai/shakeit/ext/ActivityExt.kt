package com.tsai.shakeit.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.factory.ViewModelFactory


fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as ShakeItApplication).shakeItRepository
    return ViewModelFactory(repository = repository)
}

