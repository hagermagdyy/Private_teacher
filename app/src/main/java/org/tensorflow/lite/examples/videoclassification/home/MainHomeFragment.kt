package org.tensorflow.lite.examples.videoclassification.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.videoclassification.lessons.LessonsView
import org.tensorflow.lite.examples.videoclassification.ProfileView
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.tensorflow.lite.examples.videoclassification.databinding.MainHomeViewBinding

class MainHomeFragment : Fragment() {

    private val views: Map<Int, Fragment> by lazy {
        mapOf(
            R.string.videos to LessonsView(),
            R.string.profile to ProfileView()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MainHomeViewBinding
            .inflate(inflater, container, false)

        setupViews(binding)

        return binding.root
    }

    private fun setupViews(binding: MainHomeViewBinding) {
        binding.viewPagerPackages.isUserInputEnabled = false
        binding.viewPagerPackages.adapter = ViewPagerAdapter(this, views.values)

        binding.toolbar.title = getString(R.string.main)
        TabLayoutMediator(binding.sectionsTab, binding.viewPagerPackages) { tab, index ->
            tab.setText(views.keys.elementAt(index))
        }.attach()
    }
}