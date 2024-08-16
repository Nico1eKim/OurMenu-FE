package com.example.ourmenu.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.data.PostData
import com.example.ourmenu.data.community.CommunityResponseData
import com.example.ourmenu.databinding.ItemPostBinding

class MypageRVAdapter(
    var items: ArrayList<CommunityResponseData>,val context : Context,
    val itemClickListener: (CommunityResponseData) -> Unit,
) : RecyclerView.Adapter<MypageRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPostBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityResponseData) {
            binding.tvItemPostTitle.text = item.articleTitle
            binding.tvItemPostContent.text = item.articleContent
//            binding.sivItemPostProfile.setImageResource(item.userImgUrl)
//            binding.tvItemPostUsername.text = item.username
//            binding.tvItemPostTime.text = item.time
//            binding.tvItemPostViewCount.text = item.viewCount.toString()
//            binding.sivItemPostThumbnail.setImageResource(item.thumbnail)
//            binding.tvItemPostCount.text = item.menuCount.toString()

            binding.root.setOnClickListener { itemClickListener(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }
}
