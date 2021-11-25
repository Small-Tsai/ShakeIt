package com.tsai.shakeit.ui.home.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.databinding.CommentFragmentBinding
import com.tsai.shakeit.ext.getVmFactory

class CommentFragment(private val shopId: String) : Fragment() {

    private val viewModel by viewModels<CommentViewModel> {
        getVmFactory(
            shopId = shopId
        )
    }

    private lateinit var binding: CommentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = CommentFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getComment()

        val adapter = CommentAdapter()

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        viewModel.commentList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            mainViewModel.commentCount.value = it.size
            mainViewModel.ratingAvg.value = it.map { comment -> comment.rating }.average().toFloat()
        })

        binding.commentRev.adapter = adapter
        return binding.root
    }
}
