package com.tsai.shakeit.ui.home.comment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tsai.shakeit.util.Logger

class CommentPagerAdapter(fragmentManager: FragmentManager, private val shopId: String) :
    FragmentStatePagerAdapter(
        fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    override fun getCount() = MapBottomSheetTypeFilter.values().size

    override fun getItem(position: Int): Fragment {
//        Logger.d("$shopId")
        return CommentFragment(shopId)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return MapBottomSheetTypeFilter.values()[position].value
    }
}