package com.nhm.distribution.screens.main.NBPA


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distribution.R
import com.nhm.distribution.databinding.ItemAllSchemesBinding
import com.nhm.distribution.utils.glideImagePortrait
import javax.inject.Inject

class MoviesAdapter @Inject() constructor() :
    PagingDataAdapter<MoviesListResponse.Result, MoviesAdapter.ViewHolder>(differCallback) {

    private lateinit var binding: ItemAllSchemesBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemAllSchemesBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: MoviesListResponse.Result) {
            binding.apply {
                item.foodIdentityImage?.url?.glideImagePortrait(root.context, ivIcon)
                textTitle.setText(item.name)
                textDesc.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.address_)+"</b> "+item.address.replace("\n"," "), HtmlCompat.FROM_HTML_MODE_LEGACY))
                textMobile.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.mobile_no_per)+"</b> "+item.mobileNumber, HtmlCompat.FROM_HTML_MODE_LEGACY))

                root.setOnClickListener {
                    root.findNavController().navigate(R.id.action_nbpaList_to_nbpaDetail, Bundle().apply {
                        putParcelable("key", item)
                    })
//                    onItemClickListener?.let {
//                        it(item)
//                    }
                }
            }
        }
    }

    private var onItemClickListener: ((MoviesListResponse.Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (MoviesListResponse.Result) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<MoviesListResponse.Result>() {
            override fun areItemsTheSame(oldItem: MoviesListResponse.Result, newItem: MoviesListResponse.Result): Boolean {
                return oldItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: MoviesListResponse.Result, newItem: MoviesListResponse.Result): Boolean {
                return oldItem == newItem
            }
        }
    }

}