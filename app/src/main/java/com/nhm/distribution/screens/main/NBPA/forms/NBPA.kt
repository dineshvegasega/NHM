package com.nhm.distribution.screens.main.NBPA.forms

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nhm.distribution.R
import com.nhm.distribution.databinding.NbpaBinding
import com.nhm.distribution.screens.interfaces.CallBackListener
import com.nhm.distribution.screens.main.distribution.CreateDistributionAdapter
import com.nhm.distribution.screens.main.distribution.DistributionViewModel
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoadMember
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class NBPA : Fragment() , CallBackListener {
    private val viewModel: DistributionViewModel by viewModels()
    private var _binding: NbpaBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter : CreateDistributionAdapter

    companion object{
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NbpaBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        callBackListener = this
        binding.apply {
            adapter = CreateDistributionAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.isUserInputEnabled = false
            adapter.addFragment(NBPA_Form1())
            adapter.addFragment(NBPA_Form2())
            adapter.addFragment(NBPA_Form3())

            Handler(Looper.getMainLooper()).postDelayed({
                introViewPager.adapter=adapter
                val array = listOf<String>(
                    getString(R.string.beneficiary_card),
                    getString(R.string.checkup),
                    getString(R.string.food_items),
                )
                TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                    tab.text = array[position]
                    tab.view.isEnabled = false
                }.attach()
            }, 100)


            binding.tabLayout.touchables.forEach { it.isClickable = false }

            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }

    }

    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 1000) {
                introViewPager.setCurrentItem(0, false)
            } else if (pos == 1001) {
                introViewPager.setCurrentItem(1, false)
            } else if (pos == 1002) {
                introViewPager.setCurrentItem(21, false)
            }
        }
    }




//    override fun onStop() {
//        super.onStop()
//        isProductLoad = true
//        isProductLoadMember = true
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isProductLoad = false
//        isProductLoadMember = false
//    }


}