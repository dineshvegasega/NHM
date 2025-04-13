package com.nhm.distribution.screens.main.members

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nhm.distribution.R
import com.nhm.distribution.databinding.DialogFilterBinding
import com.nhm.distribution.databinding.FragmentMoviesBinding
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.utils.showDropDownDialog
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class MemberFragment : Fragment() {

    private lateinit var binding: FragmentMoviesBinding

    @Inject
    lateinit var moviesAdapter: MemberAdapter


    private val viewModel: MemberMoviesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMoviesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text =
                getString(R.string.all_distributors)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.btFilter.visibility = View.GONE
            inclideHeaderSearch.btFilter.singleClick {
                val dialogBinding = DialogFilterBinding.inflate(
                    root.context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    ) as LayoutInflater
                )
                val dialog = BottomSheetDialog(root.context)
                dialog.setContentView(dialogBinding.root)
                dialog.setOnShowListener { dia ->
                    val bottomSheetDialog = dia as BottomSheetDialog
                    val bottomSheetInternal: FrameLayout =
                        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                    bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
                }
                dialog.show()

                dialogBinding.apply {

                    editTextStartDate.singleClick {
                        requireActivity().showDropDownDialog(type = 20) {
                            editTextStartDate.setText(title)
                        }
                    }

                    editTextEndDate.singleClick {
                        requireActivity().showDropDownDialog(type = 20) {
                            editTextEndDate.setText(title)
                        }
                    }
                    radioButtonName.isChecked = viewModel.filterNameBoolean
                    radioButtonMobile.isChecked = viewModel.filterMobileBoolean
                    radioButtonAadhaar.isChecked = viewModel.filterAadhaarBoolean
                    radioButtonDate.isChecked =
                        if (viewModel.filterStartDateBoolean || viewModel.filterEndDateBoolean) {
                            true
                        } else {
                            false
                        }

                    editTextName.setText(viewModel.filterName)
                    editTextMobile.setText(viewModel.filterMobile)
                    editTextAadhaar.setText(viewModel.filterAadhaar)
                    editTextStartDate.setText(viewModel.filterStartDate)
                    editTextEndDate.setText(viewModel.filterEndDate)

                    btClose.singleClick {
                        editTextName.setText("")
                        editTextMobile.setText("")
                        editTextAadhaar.setText("")
                        editTextStartDate.setText("")
                        editTextEndDate.setText("")
                        viewModel.filterName = ""
                        viewModel.filterMobile = ""
                        viewModel.filterAadhaar = ""
                        viewModel.filterStartDate = ""
                        viewModel.filterEndDate = ""
                        viewModel.filterNameBoolean = false
                        viewModel.filterMobileBoolean = false
                        viewModel.filterAadhaarBoolean = false
                        viewModel.filterStartDateBoolean = false
                        viewModel.filterEndDateBoolean = false
                        dialog.dismiss()
                    }


                    btApply.singleClick {
                        viewModel.filterName = editTextName.text.toString()
                        viewModel.filterMobile = editTextMobile.text.toString()
                        viewModel.filterAadhaar = editTextAadhaar.text.toString()
                        viewModel.filterStartDate = editTextStartDate.text.toString()
                        viewModel.filterEndDate = editTextEndDate.text.toString()

                        viewModel.filterNameBoolean = radioButtonName.isChecked
                        viewModel.filterMobileBoolean = radioButtonMobile.isChecked
                        viewModel.filterAadhaarBoolean = radioButtonAadhaar.isChecked
                        viewModel.filterStartDateBoolean = radioButtonDate.isChecked
                        viewModel.filterEndDateBoolean = radioButtonDate.isChecked

                        if (radioButtonDate.isChecked){
                            try{
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val startTimeInMillis = dateFormat.parse(viewModel.filterStartDate).time.toString()
                                val endTimeInMillis = dateFormat.parse(viewModel.filterEndDate).time.toString()
                                Log.e("TAG", "startTimeInMillis "+startTimeInMillis)
                                Log.e("TAG", "endTimeInMillis "+endTimeInMillis)
                                if(startTimeInMillis > endTimeInMillis){
                                    Log.e("TAG", "AAAAAA "+startTimeInMillis)
                                    dialog.dismiss()
                                    showSnackBar(dialogBinding.root.resources.getString(R.string.EndDateshouldbegreaterthanStartDate))
                                } else {
                                    Log.e("TAG", "BBBBBB "+startTimeInMillis)
                                    lifecycleScope.launch {
                                        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                            viewModel.fetch(
                                                if (radioButtonName.isChecked) viewModel.filterName else "" ,
                                                if (radioButtonMobile.isChecked) viewModel.filterMobile else "",
                                                if (radioButtonAadhaar.isChecked) viewModel.filterAadhaar else "",
                                                if (radioButtonDate.isChecked) viewModel.filterStartDate+" 00:00:00" else "",
                                                if (radioButtonDate.isChecked) viewModel.filterEndDate+" 23:59:59" else "",
                                            ).collect {
                                                moviesAdapter.submitData(it)
                                                moviesAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }

                                    lifecycleScope.launch {
                                        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                            moviesAdapter.loadStateFlow.collect {
                                                val state = it.refresh
//                                                prgBarMovies.isVisible = state is LoadState.Loading
                                                binding.idDataNotFound.root.isVisible = if (moviesAdapter.itemCount > 0) false else true
                                            }
                                        }
                                    }

                                    dialog.dismiss()
                                }
                            }catch (e : Exception){
                                Log.e("TAG", "CCCCCC "+e.message)
                                dialog.dismiss()
                                showSnackBar(dialogBinding.root.resources.getString(R.string.SelectValidStartDateandEndDate))
                            }
                        } else {
                            lifecycleScope.launch {
                                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                    viewModel.fetch(
                                        if (radioButtonName.isChecked) viewModel.filterName else "" ,
                                        if (radioButtonMobile.isChecked) viewModel.filterMobile else "",
                                        if (radioButtonAadhaar.isChecked) viewModel.filterAadhaar else "",
                                        "",
                                        "",
                                    ).collect {
                                        moviesAdapter.submitData(it)
                                        moviesAdapter.notifyDataSetChanged()
                                    }
                                }
                            }

                            lifecycleScope.launch {
                                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                    moviesAdapter.loadStateFlow.collect {
                                        val state = it.refresh
//                                        prgBarMovies.isVisible = state is LoadState.Loading
                                        binding.idDataNotFound.root.isVisible = if (moviesAdapter.itemCount > 0) false else true
                                    }
                                }
                            }

                            dialog.dismiss()
                        }


                    }

                }
            }


            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                    viewModel.moviesList.collect {
//                        moviesAdapter.submitData(it)
//                    }

                    viewModel.fetch("","","","","").collect {
                        moviesAdapter.submitData(it)
//                        moviesAdapter.notifyDataSetChanged()
                    }
                }
            }


//            lifecycleScope.launch {
//                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                    moviesAdapter.loadStateFlow.collect {
//                        val state = it.refresh
//                        prgBarMovies.isVisible = state is LoadState.Loading
//                        binding.idDataNotFound.root.isVisible = if (moviesAdapter.itemCount > 0) false else true
//                    }
//                }
//            }
//
//            rlMovies.apply {
//                layoutManager = LinearLayoutManager(requireContext())
//                adapter = moviesAdapter
//            }
//
//            rlMovies.adapter = moviesAdapter.withLoadStateFooter(
//                MemberLoadMoreAdapter {
//                    moviesAdapter.retry()
//                }
//            )


        }
    }

}