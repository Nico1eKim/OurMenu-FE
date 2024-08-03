package com.example.ourmenu.menu.menuFolder.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.databinding.ItemMenuFolderBinding
import com.example.ourmenu.menu.iteminterface.MenuItemClickListener

class PostMenuFolderGetRVAdapter(private val items: ArrayList<DummyMenuData>) :
    RecyclerView.Adapter<PostMenuFolderGetRVAdapter.ViewHolder>() {

    lateinit var itemClickListener: MenuItemClickListener

    fun setOnItemClickListener(onItemListener: MenuItemClickListener) {
        itemClickListener = onItemListener
    }

    inner class ViewHolder(val binding: ItemMenuFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DummyMenuData) {
            binding.ivItemMenuFolderImage
            binding.tvItemMenuFolderMenuCount.text = item.menuCount.toString()
            binding.tvItemMenuFolderTitle.text = item.title


            binding.ivItemMenuFolderImage.setOnClickListener {
                itemClickListener.onMenuClick()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostMenuFolderGetRVAdapter.ViewHolder {
        val binding = ItemMenuFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostMenuFolderGetRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}
