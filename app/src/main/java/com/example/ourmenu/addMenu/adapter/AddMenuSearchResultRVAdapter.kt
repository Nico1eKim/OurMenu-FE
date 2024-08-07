package com.example.ourmenu.addMenu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.place.PlaceSearchData
import com.example.ourmenu.databinding.ItemAddMenuSearchResultBinding

class AddMenuSearchResultRVAdapter(
    var items: ArrayList<PlaceSearchData>,
    val itemClickListener: (PlaceSearchData) -> Unit,
) : RecyclerView.Adapter<AddMenuSearchResultRVAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemAddMenuSearchResultBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        //        fun bind(item: PlaceInfoData2) {
//            binding.tvAddMenuSearchResultPlace.text = item.name
//            binding.tvAddMenuSearchResultAddress.text = item.address
//
//            binding.root.setOnClickListener { itemClickListener(item) }
//        }
        fun bind(item: PlaceSearchData) {
//            if (item is PlaceSearchHistoryData) {
//                binding.tvAddMenuSearchResultPlace.text = item.storeName
//                binding.tvAddMenuSearchResultAddress.text = item.address
//                binding.root.setOnClickListener { itemClickListener(item) }
//            } else if (item is PlaceInfoData2) {
//                binding.tvAddMenuSearchResultPlace.text = item.name
//                binding.tvAddMenuSearchResultAddress.text = item.address
//                binding.root.setOnClickListener { itemClickListener(item) }
//            }

            binding.tvAddMenuSearchResultPlace.text = item.placeTitle
            binding.tvAddMenuSearchResultAddress.text = item.placeAddress
            binding.root.setOnClickListener { itemClickListener(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemAddMenuSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    fun updateItemsFromSearch(newItems: ArrayList<PlaceSearchData>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

//    fun updateItemsFromSearchResults(newItems: ArrayList<PlaceInfoData2>) {
//        items.clear()
//        items.addAll(newItems)
//        notifyDataSetChanged()
//    }
}
