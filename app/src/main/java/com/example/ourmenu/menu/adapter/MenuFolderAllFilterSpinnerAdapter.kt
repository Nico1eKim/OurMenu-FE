package com.example.ourmenu.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ourmenu.R
import com.example.ourmenu.databinding.CommunityFilterSpinnerDefaultBinding
import com.example.ourmenu.databinding.CommunityFilterSpinnerItemBinding
import com.example.ourmenu.databinding.SpinnerItemBackgroundBinding
import com.example.ourmenu.databinding.SpinnerItemBackgroundNullBinding
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.viewGone


class MenuFolderAllFilterSpinnerAdapter<T>(
    private val context: Context,
    private val items: List<String>,
) :
    ArrayAdapter<String>(context, R.layout.spinner_default_background, items) {
    var selectedPos = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 스피너의 기본 선택된 항목을 위한 뷰
        val binding = CommunityFilterSpinnerDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.itemCfsdDropdownText.text = this.getItem(position)

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 스피너의 드롭다운 목록을 위한 뷰
        val binding = CommunityFilterSpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 최신순
        binding.itemCfsiText.text = items[position]

        if (selectedPos == position) {
            binding.itemCfsiText.setTextColor(
                ContextCompat.getColor(context, R.color.Primary_500_main)
            )
        } else {
            binding.itemCfsiText.setTextColor(
                ContextCompat.getColor(context, R.color.Neutral_500)
            )
        }
        if(position == 2){
            binding.itemCfsiLine.viewGone()

        }

        binding.root.setBackgroundColor(Color.TRANSPARENT)


        return binding.root
    }


}

