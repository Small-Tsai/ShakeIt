package com.tsai.shakeit.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.R

class LoginFragment : Fragment() {


    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler().postDelayed({
            findNavController().navigate(LoginFragmentDirections.navToHome())
        }, 2000L)



    return inflater.inflate(R.layout.login_fragment, container, false)
}
}