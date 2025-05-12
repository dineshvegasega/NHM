package com.nhm.distribution.screens.main.NBPA.forms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nhm.distribution.R
import com.nhm.distribution.databinding.CheckDetailsBinding
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.screens.mainActivity.MainActivity.Companion.navHostFragment
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckDetails  : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: CheckDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CheckDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)

        binding.apply {
            btSignIn.singleClick {
                findNavController().navigate(R.id.action_checkDetails_to_nbpa)
            }
        }
    }
}