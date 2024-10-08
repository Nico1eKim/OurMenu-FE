package com.example.ourmenu.community

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.community.adapter.CommunityFilterSpinnerAdapter
import com.example.ourmenu.community.write.CommunityWritePostActivity
import com.example.ourmenu.data.community.ArticleResponse
import com.example.ourmenu.data.community.CommunityResponse
import com.example.ourmenu.data.community.CommunityResponseData
import com.example.ourmenu.data.user.UserResponse
import com.example.ourmenu.databinding.FragmentCommunityBinding
import com.example.ourmenu.mypage.adapter.MypageRVAdapter
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.CommunityService
import com.example.ourmenu.retrofit.service.UserService
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Response

class CommunityFragment : Fragment() {
    var userName: String? = null
    lateinit var binding: FragmentCommunityBinding
    var items: ArrayList<CommunityResponseData> = ArrayList()
    var page = 0
    var item: CommunityResponseData? = null
    var searchContent = ""
    var myEmail = ""

    override fun onResume() {
        super.onResume()
        getCommunity("")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        initPostList()
        initSpinner()
        initListener()
        initSearch()

        return binding.root
    }

    fun initSearch() {
        binding.etCommunitySearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchContent = v.text.toString()
                initPostList()
                true
            } else {
                false
            }
        }
    }

    private fun initSpinner() {
        val adapter =
            CommunityFilterSpinnerAdapter<String>(requireContext(), arrayListOf("최신순", "조회순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnCommunityFilter.adapter = adapter
        binding.spnCommunityFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (id.toInt() == 0) {
                    initPostList()
                } else {
                    initPostList("VIEWS_DESC")
                }
                adapter.isNewest = position == 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    fun initPostList(option: String = "CREATED_AT_DESC") {
        initRV()
        page = 0
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call = service.getCommunity(searchContent, page++, 5, option)
        call.enqueue(object : retrofit2.Callback<CommunityResponse> {
            override fun onResponse(call: Call<CommunityResponse>, response: Response<CommunityResponse>) {
                if (response.isSuccessful) {
                    items.clear()
                    for (i in response.body()?.response!!) {
                        Log.d("오류", i.toString())
                        item = CommunityResponseData(
                            i.articleId,
                            i.articleTitle,
                            i.articleContent,
                            i.userNickname,
                            i.userImgUrl,
                            i.createdBy,
                            i.menusCount,
                            i.articleViews,
                            i.articleThumbnail
                        )
                        items.add(item!!)
                    }
                    binding.rvCommunity.adapter?.notifyDataSetChanged()
                } else {
                    Log.d("오류", response.body()?.errorResponse?.message.toString())
                }
            }

            override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


    fun getCommunity(str: String) {
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call = service.getCommunity(str, page++, 5)
        call.enqueue(object : retrofit2.Callback<CommunityResponse> {
            override fun onResponse(call: Call<CommunityResponse>, response: Response<CommunityResponse>) {
                if (response.isSuccessful) {
                    for (i in response.body()?.response!!) {
                        item = CommunityResponseData(
                            i.articleId,
                            i.articleTitle,
                            i.articleContent,
                            i.userNickname,
                            i.userImgUrl,
                            i.createdBy,
                            i.menusCount,
                            i.articleViews,
                            i.articleThumbnail
                        )
                        items.add(item!!)
                    }
                    binding.rvCommunity.adapter?.notifyDataSetChanged()
                } else {
                    Log.d("오류", response.body()?.errorResponse?.message.toString())
                }
            }

            override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initListener() {
        binding.ivCommunityWrite.setOnClickListener {
            val intent = Intent(context, CommunityWritePostActivity::class.java)
            intent.putExtra("flag", "write")
            startActivity(intent)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun initRV() {
        val adapter =
            MypageRVAdapter(items, requireContext()) {
                // TODO: 해당 게시물로 이동하기
                val intent = Intent(context, CommunityWritePostActivity::class.java)
                    if (myEmail == userName) {
                        intent.putExtra("isMine", true)
                        intent.putExtra("ArticleId", it.articleId)
                        intent.putExtra("postData", it)
                        intent.putExtra("flag", "post")
                        startActivity(intent)
                    } else {
                        intent.putExtra("isMine", false)
                        intent.putExtra("ArticleId", it.articleId)
                        intent.putExtra("postData", it)
                        intent.putExtra("flag", "post")
                        startActivity(intent)

                }
            }

        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    // 스크롤이 끝났을 때 추가 데이터를 로드
                    getCommunity(searchContent)
                }
            }
        })
    }


}
