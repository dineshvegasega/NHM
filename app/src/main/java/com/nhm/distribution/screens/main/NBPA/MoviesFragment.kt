package com.nhm.distribution.screens.main.NBPA

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhm.distribution.R
import com.nhm.distribution.databinding.FragmentMoviesBinding
import com.nhm.distribution.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private lateinit var binding: FragmentMoviesBinding

    @Inject
    lateinit var moviesAdapter: MoviesAdapter



    private val viewModel: MoviesViewModel by viewModels()


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
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.Nikshay_Beneficiary_Program_Activities)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE


            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.moviesList.collect {
                        moviesAdapter.submitData(it)
                    }
                }
            }
//            lifecycleScope.launchWhenCreated {
//                viewModel.moviesList.collect {
//                    moviesAdapter.submitData(it)
//                }
//            }

//            moviesAdapter.setOnItemClickListener {
////                val direction = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(it.id)
////                findNavController().navigate(direction)
//            }

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    moviesAdapter.loadStateFlow.collect{
                        val state = it.refresh
                        prgBarMovies.isVisible = state is LoadState.Loading
                    }
                }
            }

//            lifecycleScope.launchWhenCreated {
//                moviesAdapter.loadStateFlow.collect{
//                    val state = it.refresh
//                    prgBarMovies.isVisible = state is LoadState.Loading
//                }
//            }


            rlMovies.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = moviesAdapter
            }

            rlMovies.adapter=moviesAdapter.withLoadStateFooter(
                LoadMoreAdapter{
                    moviesAdapter.retry()
                }
            )

        }
    }

}