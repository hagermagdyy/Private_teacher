package org.tensorflow.lite.examples.videoclassification

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter constructor(
    parent: Fragment,
    val fragments: Collection<Fragment>
) : FragmentStateAdapter(parent) {

    override fun getItemCount(): Int {
        return fragments.count()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.elementAt(position)
    }

    fun getItemHeight(position: Int): Int? {
        return fragments.elementAt(position).view?.measuredHeight
    }
}
