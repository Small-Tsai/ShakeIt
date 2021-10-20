package com.tsai.shakeit.ui.Detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsai.shakeit.R

class DrinksDetailFragment : Fragment() {

    companion object {
        fun newInstance() = DrinksDetailFragment()
    }

    private lateinit var viewModel: DrinksDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drinks_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DrinksDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}