package com.example.ourmenu.menu.menuFolder.post

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.ourmenu.R
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.databinding.FragmentPostMenuFolderGetDetailBinding
import com.example.ourmenu.menu.menuFolder.post.adapter.PostMenuFolderGetDetailRVAdapter

class PostMenuFolderGetDetailFragment(val postMenuFolderFragment: PostMenuFolderFragment) : Fragment() {

    lateinit var binding: FragmentPostMenuFolderGetDetailBinding
    lateinit var dummyItems: ArrayList<DummyMenuData>
    lateinit var postMenuFolderGetDetailRVAdapter: PostMenuFolderGetDetailRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostMenuFolderGetDetailBinding.inflate(layoutInflater)

        initDummy()
        initRV()
        initListener()
        checkButtonEnabled()






        return binding.root
    }

    private fun checkButtonEnabled() {
        // 체크된 항목이 없으면 비활성화,
        // 체크된 항목이 있으면 활성화
        binding.btnPmfgdAddMenu.isEnabled = postMenuFolderGetDetailRVAdapter.checkedItems.isNotEmpty()
    }

    private fun initListener() {
        binding.ivPmfgdBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnPmfgdAddMenu.setOnClickListener {

            val bundle = Bundle().apply {
                putSerializable(
                    "checkedItems", postMenuFolderGetDetailRVAdapter.checkedItems
                )
            }

            postMenuFolderFragment.apply {
                arguments = bundle
            }

            with(parentFragmentManager) {
                // 백스택 제거
                popBackStack("PostMenuFolderFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                popBackStack("PostMenuFolderGetFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                beginTransaction()
                    .replace(R.id.post_menu_folder_frm, postMenuFolderFragment)
                    .commit()
            }
        }
    }

    private fun initDummy() {
        dummyItems = ArrayList<DummyMenuData>()
        for (i in 1..5) {
            dummyItems.add(
                DummyMenuData(menu = "화산라멘", store = "화산점", address = "서울 광진구")
            )
        }
    }

    // TODO string.xml 에 저장하고 placeholder 로 사용권장.
    @SuppressLint("SetTextI18n")
    private fun initRV() {
        postMenuFolderGetDetailRVAdapter = PostMenuFolderGetDetailRVAdapter(dummyItems).apply {
            setOnItemClickListener {
                checkButtonEnabled()
            }
        }
        binding.rvPmfgdList.adapter = postMenuFolderGetDetailRVAdapter

        // 메뉴 갯수 조정
        binding.tvPmfgdNumber.text = "메뉴 ${dummyItems.size}개"
    }
}
