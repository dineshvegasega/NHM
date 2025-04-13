package com.nhm.distribution.screens.main.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distribution.databinding.LoadMoreTintBinding

class MemberLoadMoreAdapter(private val retry: () -> Unit) : LoadStateAdapter<MemberLoadMoreAdapter.ViewHolder>() {

    private lateinit var binding: LoadMoreTintBinding

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        binding = LoadMoreTintBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(retry)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.setData(loadState)
    }

    inner class ViewHolder(retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnLoadMoreRetry.setOnClickListener { retry() }
        }

        fun setData(state: LoadState) {
            binding.apply {
                prgBarLoadMore.isVisible = state is LoadState.Loading
                tvLoadMore.isVisible = state is LoadState.Error
                btnLoadMoreRetry.isVisible = state is LoadState.Error
            }
        }
    }
}