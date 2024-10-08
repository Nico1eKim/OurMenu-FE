package com.example.ourmenu.menu.menuFolder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.data.BaseResponse
import com.example.ourmenu.data.menuFolder.data.MenuFolderData
import com.example.ourmenu.data.menuFolder.response.MenuFolderArrayResponse
import com.example.ourmenu.databinding.CommunityDeleteDialogBinding
import com.example.ourmenu.databinding.FragmentMenuFolderBinding
import com.example.ourmenu.menu.adapter.MenuFolderRVAdapter
import com.example.ourmenu.menu.callback.SwipeItemTouchHelperCallback
import com.example.ourmenu.menu.iteminterface.MenuFolderItemClickListener
import com.example.ourmenu.menu.menuFolder.post.PostMenuFolderActivity
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuFolderService
import com.example.ourmenu.util.Utils
import com.example.ourmenu.util.Utils.dpToPx
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFolderFragment : Fragment() {
    lateinit var binding: FragmentMenuFolderBinding
    lateinit var itemClickListener: MenuFolderItemClickListener
    private val menuFolderItems = ArrayList<MenuFolderData>()
    lateinit var rvAdapter: MenuFolderRVAdapter
    lateinit var swipeItemTouchHelperCallback: SwipeItemTouchHelperCallback
    private var allMenuCount = 0

    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)

//    override fun onStart() {
//        super.onStart()
//        if (menuFolderItems.size > 0) {
//            getMenuFolders()
//        }
//    }

    override fun onResume() {
        super.onResume()
        if (menuFolderItems.size > 0) {
            binding.nsvMenuFolder.post {
                binding.nsvMenuFolder.scrollTo(0, 0)  // 즉시 맨 위로 이동
                // 또는 부드럽게 이동하고 싶으면
                // nestedScrollView.smoothScrollTo(0, 0)
            }
            getMenuFolders()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuFolderBinding.inflate(inflater, container, false)

        getMenuFolders()
        initTouchHelper()
        initItemListener()

//        Handler(Looper.getMainLooper()).postDelayed({
//            // 일정 시간이 지나면 MainActivity로 이동
//            getMenuFolders()
//            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
//            // 이동한 다음 사용안함으로 finish 처리
//        }, 1000) // 시간 1초 이후 실행

        return binding.root
    }

    private fun getMenuFolders() {
        menuFolderService.getMenuFolders().enqueue(
            object : Callback<MenuFolderArrayResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<MenuFolderArrayResponse>,
                    response: Response<MenuFolderArrayResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val menuFolders = result?.response
                        menuFolders?.let {
                            if (menuFolderItems.size + 1 == menuFolders.menuFolders.size) {
                                menuFolderItems.add(
                                    menuFolders.menuFolders[menuFolders.menuFolders.size - 1]
                                )
                                initRV()
                                rvAdapter.notifyItemInserted(menuFolderItems.size - 1)
                            }

                            if (menuFolderItems.size == 0) {
                                menuFolderItems.addAll(menuFolders.menuFolders)
                                for (menuFolder in menuFolderItems) {
                                    allMenuCount = menuFolders.menuCount
                                }
                                binding.tvMenuFolderAllMenuCount.text = "메뉴 ${allMenuCount}개"
                                initRV()
                                rvAdapter.updateList(menuFolders.menuFolders)
                            }
                            if (menuFolderItems.size == menuFolders.menuFolders.size) {
                                rvAdapter.updateList(menuFolders.menuFolders)

                            }

                        }
                    } else {
                        Log.d("err", response.errorBody().toString())
                    }
                }

                override fun onFailure(
                    call: Call<MenuFolderArrayResponse>,
                    t: Throwable,
                ) {
                    Log.d("menuFolders", t.message.toString())
                }
            },
        )
    }

    private fun deleteMenuFolder(
        menuFolderId: Int,
        position: Int,
    ) {
        menuFolderService.deleteMenuFolder(menuFolderId).enqueue(
            object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("deleteMenuFolder", result.toString())
                        menuFolderItems.removeAt(position)
                        rvAdapter.notifyItemRemoved(position)
                        rvAdapter.notifyItemRangeRemoved(position, menuFolderItems.size - position)
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable,
                ) {
                    Log.d("deleteMenuFolder", t.toString())
                }
            },
        )
    }

    private fun initItemListener() {
        // 상단 메뉴 추가
        binding.ivMenuAddMenu.setOnClickListener {
            // TODO API 추가
            val intent = Intent(context, AddMenuActivity::class.java)
            startActivity(intent)
        }

        // 메뉴판 추가하기
        binding.btnMenuAddMenuFolder.setOnClickListener {
            val intent = Intent(context, PostMenuFolderActivity::class.java)
            startActivity(intent)
        }

        // 전체 메뉴판 보기
        binding.ivMenuAllMenu.setOnClickListener {
            val intent = Intent(context, MenuFolderDetailActivity::class.java)
            intent.putExtra("isAll", true)
            startActivity(intent)
        }

        itemClickListener =
            object : MenuFolderItemClickListener {
                override fun onMenuClick(
                    menuFolderId: Int,
                    menuFolderTitle: String?,
                    menuFolderImgUrl: String?,
                ) {
                    val intent = Intent(context, MenuFolderDetailActivity::class.java)
                    intent.putExtra("menuFolderId", menuFolderId)
                    intent.putExtra("menuFolderTitle", menuFolderTitle)
                    intent.putExtra("menuFolderImgUrl", menuFolderImgUrl)
                    startActivity(intent)
                }

                override fun onEditClick(menuFolderId: Int) {
                    // MenuFolderFragment 에서 editClick() 메소드 실행
                    Log.d("edit", menuFolderId.toString())
                    val intent = Intent(context, MenuFolderDetailActivity::class.java)
                    intent.putExtra("menuFolderId", menuFolderId)
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }

                override fun onDeleteClick(
                    menuFolderId: Int,
                    position: Int,
                ) {
                    Log.d("delete", menuFolderId.toString())
                    // 경고 BottomSheetDialog 표시
                    showDeleteDialog(menuFolderId, position)
                }
            }
    }

    private fun showDeleteDialog(
        menuFolderId: Int,
        position: Int,
    ) {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { Utils.applyBlurEffect(it) }

        val dialogBinding = CommunityDeleteDialogBinding.inflate(LayoutInflater.from(context))
        val deleteDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        deleteDialog.setOnShowListener {
            val window = deleteDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }
        deleteDialog.setOnDismissListener {
            rootView?.let { Utils.removeBlurEffect(it) }
        }

        // '삭제' 버튼 클릭 시 삭제 작업 수행
        dialogBinding.btnCddDelete.setOnClickListener {
            deleteMenuFolder(menuFolderId, position)
            deleteDialog.dismiss()
            rootView?.let { Utils.removeBlurEffect(it) }
        }

        // '취소' 버튼 클릭 시 다이얼로그 닫기
        dialogBinding.btnCddCancel.setOnClickListener {
            deleteDialog.dismiss()
            rootView?.let { Utils.removeBlurEffect(it) }
        }

        deleteDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility") // 이줄 없으면 setOnTouchListener 에 밑줄생김
    private fun initRV() {
        rvAdapter =
            MenuFolderRVAdapter(
                menuFolderItems,
                requireContext(),
                swipeItemTouchHelperCallback,
            ).apply {
                setOnItemClickListener(itemClickListener)
            }

        Log.d("mi", menuFolderItems.size.toString())

        swipeItemTouchHelperCallback.setAdapter(rvAdapter)

        val itemTouchHelper = ItemTouchHelper(swipeItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvMenuMenuFolder)
        // 리사이클러 뷰 설정
        with(binding.rvMenuMenuFolder) {
            // 다른 뷰를 건들면 기존 뷰의 swipe 가 초기화 됨
            setOnTouchListener { _, _ ->
                swipeItemTouchHelperCallback.removePreviousClamp(this)
//                rvAdapter.setOnItemClickListener(itemClickListener)
                false
            }
            adapter = rvAdapter
        }
    }

    //
    private fun initTouchHelper() {
        val clamp: Float = dpToPx(requireContext(), 120).toFloat()

        swipeItemTouchHelperCallback =
            SwipeItemTouchHelperCallback().apply {
                setClamp(clamp)
            }
    }
}
