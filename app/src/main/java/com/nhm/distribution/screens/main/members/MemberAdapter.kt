package com.nhm.distribution.screens.main.members


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distribution.R
import com.nhm.distribution.databinding.ItemAllSchemesBinding
import com.nhm.distribution.utils.changeDateFormat
import com.nhm.distribution.utils.glideImagePortrait
import javax.inject.Inject

class MemberAdapter @Inject() constructor() :
    PagingDataAdapter<MemberMoviesListResponse.Result, MemberAdapter.ViewHolder>(differCallback) {

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
        fun bind(item: MemberMoviesListResponse.Result) {
            binding.apply {
                ivIcon.visibility = View.GONE
                item.foodIdentityImage?.url?.glideImagePortrait(root.context, ivIcon)
                textTitle.setText(item.name)
                textDesc.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.aadhaar_no)+"</b> "+item.aadhar_card, HtmlCompat.FROM_HTML_MODE_LEGACY))
                textMobile.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.mobile_no_per)+"</b> "+item.mobile_no, HtmlCompat.FROM_HTML_MODE_LEGACY))
                item.updated_date?.let {
                    textValidDateValue.text = "${item.updated_date.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")}"
                }
//                root.setOnClickListener {
//                    root.findNavController().navigate(R.id.action_nbpaList_to_nbpaDetail, Bundle().apply {
//                        putParcelable("key", item)
//                    })
////                    onItemClickListener?.let {
////                        it(item)
////                    }
//                }
            }
        }
    }

    private var onItemClickListener: ((MemberMoviesListResponse.Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (MemberMoviesListResponse.Result) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<MemberMoviesListResponse.Result>() {
            override fun areItemsTheSame(oldItem: MemberMoviesListResponse.Result, newItem: MemberMoviesListResponse.Result): Boolean {
                return oldItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: MemberMoviesListResponse.Result, newItem: MemberMoviesListResponse.Result): Boolean {
                return oldItem == newItem
            }
        }
    }

}