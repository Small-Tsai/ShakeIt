package com.tsai.shakeit.ui.order.sendcomment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.CommentDialogFragmentBinding
import com.tsai.shakeit.ext.getVmFactory

class CommentDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<CommentDialogViewModel> {
        getVmFactory(
            shopId = CommentDialogArgs.fromBundle(requireArguments()).shopId
        )
    }

    private lateinit var binding: CommentDialogFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CommentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = CommentDialogFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.popBack.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigateUp() }
        })

        viewModel.comment.observe(viewLifecycleOwner, {
            viewModel.send(it)
        })

        setRatingBar()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setRatingBar() {
        binding.sendRatingBar.let {
            it.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val touchPositionX = event.x
                    val width: Int = it.width
                    val starsF = touchPositionX / width * 5.0f
                    val stars = starsF.toInt() + 1
                    it.rating = stars.toFloat()
                    v.isPressed = false
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.isPressed = true
                }
                if (event.action == MotionEvent.ACTION_CANCEL) {
                    v.isPressed = false
                }
                true
            }
        }
    }
}
