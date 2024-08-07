package com.example.ourmenu.addMenu

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.adapter.AddMenuSearchResultRVAdapter
import com.example.ourmenu.data.place.PlaceDetailData
import com.example.ourmenu.data.place.PlaceDetailResponse
import com.example.ourmenu.data.place.PlaceSearchData
import com.example.ourmenu.data.place.PlaceSearchResponse
import com.example.ourmenu.databinding.FragmentAddMenuMapBinding
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.PlaceService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Response

class AddMenuMapFragment :
    Fragment(),
    OnMapReadyCallback {
    lateinit var binding: FragmentAddMenuMapBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var resultAdapter: AddMenuSearchResultRVAdapter

    private var recentSearchItems: ArrayList<PlaceSearchData> = ArrayList()
    private var searchResultItems: ArrayList<PlaceSearchData> = ArrayList()

    lateinit var placeDetailItem: PlaceDetailData

    private var isKeyboardVisible = false

    private var naverMap: NaverMap? = null
    private var marker: Marker? = null // 마커 관리를 위한 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddMenuMapBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clAddMenuBottomSheet)

        // BottomSheet의 초기 상태를 숨김으로 설정
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        initResultRV()

        // MapFragment 가져오기
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fcv_add_menu_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction().add(R.id.fcv_add_menu_map, it).commit()
                }
        mapFragment.getMapAsync(this)

        //  키보드 상태 변화 감지해서 화면 길이 조절하기
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                isKeyboardVisible = true
            } else {
                //  키보드 안보일 때
                if (isKeyboardVisible) {
                    isKeyboardVisible = false
                    adjustLayoutForKeyboardDismiss()
                }
            }
        }

        // BottomSheet 상태 변화 리스너 설정
        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                }
            },
        )

        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.ivAddMenuLogoBack.setOnClickListener {
            binding.etAddMenuSearch.text.clear() // 입력 필드 비우기 추가
            handleBackPress()
        }

        // 검색바 focus됐을 때
        binding.etAddMenuSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fetchSearchHistoryInfo() // EditText에 포커스가 갈 때 검색 기록을 가져옴

                binding.vAddMenuSearchBg.visibility = View.VISIBLE
                binding.fcvAddMenuMap.visibility = View.GONE
                binding.rvAddMenuSearchResults.visibility = View.VISIBLE
                binding.clAddMenuRecentSearch.visibility = View.VISIBLE
                binding.btnAddMenuNoResult.visibility = View.VISIBLE

                // 최근 검색 기록을 어댑터에 설정
                resultAdapter.updateItemsFromSearch(recentSearchItems)

                binding.etAddMenuSearch.text.clear() // 입력 필드 비우기

                // bottom sheet가 떠있는 상태에서 검색바를 클릭하면 bottom sheet가 사라지도록
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                binding.vAddMenuSearchBg.visibility = View.GONE
                binding.fcvAddMenuMap.visibility = View.VISIBLE
                binding.rvAddMenuSearchResults.visibility = View.GONE
                binding.clAddMenuRecentSearch.visibility = View.GONE
                binding.btnAddMenuNoResult.visibility = View.GONE
            }
        }

        // 검색바에서 엔터 키 이벤트 처리
        binding.etAddMenuSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch(binding.etAddMenuSearch.text.toString())
                true
            } else {
                false
            }
        }

        // 검색 결과 없을 때 버튼 이벤트 처리
        binding.btnAddMenuNoResult.setOnClickListener {
            binding.etAddMenuSearch.text.clear() // 입력 필드 비우기 추가

            // 키보드 숨기기
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)

            parentFragmentManager
                .beginTransaction()
                .addToBackStack("AddMenuMap")
                .replace(R.id.cl_add_menu_main, AddMenuNameFragment())
                .commit()
        }

        binding.btnAddMenuNext.setOnClickListener {
            binding.etAddMenuSearch.text.clear() // 입력 필드 비우기 추가

            parentFragmentManager
                .beginTransaction()
                .addToBackStack("AddMenuMap")
                .replace(R.id.cl_add_menu_main, AddMenuSelectMenuFragment())
                .commit()
        }

        return binding.root
    }

    private fun fetchPlaceInfo(title: String) {
        val service = RetrofitObject.retrofit.create(PlaceService::class.java)
        val call = service.getPlaceInfo("Bearer " + RetrofitObject.TOKEN, title)

        call.enqueue(
            object : retrofit2.Callback<PlaceSearchResponse> {
                override fun onResponse(
                    call: Call<PlaceSearchResponse>,
                    response: retrofit2.Response<PlaceSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        val placeInfoResponse = response.body()

                        if (placeInfoResponse?.isSuccess == true) {
                            val placeInfo = placeInfoResponse.response
                            if (placeInfo != null) {
                                searchResultItems = placeInfo
                                resultAdapter.updateItemsFromSearch(searchResultItems)
                            }
                        } else {
                            val errorMessage = placeInfoResponse?.errorResponse?.message ?: "error"
                            Log.d("오류1", errorMessage)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("오류2", errorResponse ?: "error")
                    }
                }

                override fun onFailure(
                    call: Call<PlaceSearchResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    private fun fetchSearchHistoryInfo() {
        val service = RetrofitObject.retrofit.create(PlaceService::class.java)
        val call = service.getPlaceSearchHistory("Bearer " + RetrofitObject.TOKEN)

        call.enqueue(
            object : retrofit2.Callback<PlaceSearchResponse> {
                override fun onResponse(
                    call: Call<PlaceSearchResponse>,
                    response: retrofit2.Response<PlaceSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        val searchHistoryResponse = response.body()
                        if (searchHistoryResponse?.isSuccess == true) {
                            recentSearchItems = searchHistoryResponse.response
                            resultAdapter.updateItemsFromSearch(recentSearchItems)
                        } else {
                            val errorMessage = searchHistoryResponse?.errorResponse?.message ?: "error"
                            Log.d("오류1", errorMessage)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("오류2", errorResponse ?: "error")
                    }
                }

                override fun onFailure(
                    call: Call<PlaceSearchResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    private fun fetchPlaceDetail(id: String) {
        val service = RetrofitObject.retrofit.create(PlaceService::class.java)
        val call = service.getPlaceInfoDetail("Bearer " + RetrofitObject.TOKEN, id)

        call.enqueue(
            object : retrofit2.Callback<PlaceDetailResponse> {
                override fun onResponse(
                    call: Call<PlaceDetailResponse>,
                    response: Response<PlaceDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val placeDetailResponse = response.body()

                        if (placeDetailResponse?.isSuccess == true) {
                            placeDetailItem = placeDetailResponse.response
                            Log.d("성공", placeDetailItem.toString())
                            showPlaceDetails(placeDetailItem) // 데이터가 성공적으로 받아졌을 때 UI 업데이트
                        } else {
                            val errorMessage = placeDetailResponse?.errorResponse?.message ?: "error"
                            Log.d("오류1", errorMessage)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("오류2", errorResponse ?: "error")
                    }
                }

                override fun onFailure(
                    call: Call<PlaceDetailResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    private fun initResultRV() {
        resultAdapter =
            AddMenuSearchResultRVAdapter(arrayListOf()) { place ->
                binding.etAddMenuSearch.setText(place.placeTitle)
                fetchPlaceDetail(place.placeId)
                returnToMap()
            }
        binding.rvAddMenuSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvAddMenuSearchResults.adapter = resultAdapter

        fetchSearchHistoryInfo() // 어댑터 초기화 시 검색 기록으로 설정
    }

    private fun showPlaceDetails(item: PlaceDetailData) {
        binding.tvAddMenuBsPlaceName.text = item.placeTitle
        binding.tvAddMenuBsAddress.text = item.placeAddress
        binding.tvAddMenuBsTime.text = item.timeInfo
//        binding.sivAddMenuBsImg1.setImageResource(item.imgs[0])
//        binding.sivAddMenuBsImg2.setImageResource(item.imgs[1])
//        binding.sivAddMenuBsImg3.setImageResource(item.imgs[2])

        // 이미지 로드
//        if (item.images.isNotEmpty()) {
//            binding.sivAddMenuBsImg1.visibility = View.VISIBLE
//            binding.sivAddMenuBsImg2.visibility = View.VISIBLE
//            binding.sivAddMenuBsImg3.visibility = View.VISIBLE

        // Glide를 사용해 이미지를 로드하는 예시 (Glide는 의존성 추가 필요)
//            Glide.with(this).load(item.images[0]).into(binding.sivAddMenuBsImg1)
//            Glide.with(this).load(item.images[1]).into(binding.sivAddMenuBsImg2)
//            Glide.with(this).load(item.images[2]).into(binding.sivAddMenuBsImg3)
//        } else {
        binding.sivAddMenuBsImg1.visibility = View.GONE
        binding.sivAddMenuBsImg2.visibility = View.GONE
        binding.sivAddMenuBsImg3.visibility = View.GONE
//        }

        // 지도에 핀 찍기
        val mapx = item.latitude.toDouble()
        val mapy = item.longitude.toDouble()

        // 기존 마커 제거
        marker?.map = null

        // 새로운 마커 설정
        marker =
            Marker().apply {
                position = LatLng(mapy, mapx)
                // 자체 아이콘으로 pin 설정
                icon = OverlayImage.fromResource(R.drawable.ic_map_pin_add)
                map = naverMap
            }

        // 지도의 focus를 해당 위치로 이동
        naverMap?.moveCamera(CameraUpdate.scrollTo(LatLng(mapy, mapx)))

        // 키보드가 사라질 때 Bottom Sheet가 화면의 가장 아래에 위치하도록
        binding.clAddMenuBottomSheet.postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100)
    }

    private fun adjustLayoutForKeyboardDismiss() {
        binding.root.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.root.requestLayout()
    }

    private fun returnToMap() {
        binding.rvAddMenuSearchResults.visibility = View.GONE
        binding.etAddMenuSearch.clearFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)
    }

    private fun handleBackPress() {
        if (binding.vAddMenuSearchBg.visibility == View.VISIBLE ||
            binding.rvAddMenuSearchResults.visibility == View.VISIBLE
        ) {
            // 검색 화면이 보일 때 -> 지도 화면으로 전환
            binding.vAddMenuSearchBg.visibility = View.GONE
            binding.fcvAddMenuMap.visibility = View.VISIBLE
            binding.rvAddMenuSearchResults.visibility = View.GONE
            binding.clAddMenuRecentSearch.visibility = View.GONE
            binding.etAddMenuSearch.clearFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)

            // 기존 마커 제거
            marker?.map = null
        } else {
            // 지도 화면이 보일 때 -> 이전 화면으로 돌아가기
            requireActivity().onBackPressed()
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            binding.rvAddMenuSearchResults.visibility = View.GONE
            binding.clAddMenuNoResult.visibility = View.VISIBLE
        } else {
            binding.clAddMenuRecentSearch.visibility = View.GONE
            fetchPlaceInfo(query)
        }

        // 검색 하면 키보드 숨기기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
    }
}
