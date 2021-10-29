package com.tsai.shakeit.ui.order.sendcomment

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.CommentDialogFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = CommentDialogFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.popBack.observe(viewLifecycleOwner, Observer {
            Logger.d("popback")
            it?.let { findNavController().navigateUp() }
        })

        viewModel.comment.observe(viewLifecycleOwner, Observer {
            viewModel.send(it)
        })

        setRatingBar()

        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setRatingBar() {
        binding.sendRatingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
                Toast.makeText(
                    context,
                    "Given rating is: $p1",
                    Toast.LENGTH_SHORT
                ).show()
            }

        binding.sendRatingBar.let {
            it.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val touchPositionX = event.x
                    val width: Int = it.getWidth()
                    val starsf = touchPositionX / width * 5.0f
                    val stars = starsf.toInt() + 1
                    it.setRating(stars.toFloat())
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