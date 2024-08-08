package com.example.ourmenu.addMenu

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnFocusChangeListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.iterator
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.bottomSheetClass.AddMenuBottomSheetIcon
import com.example.ourmenu.databinding.FragmentAddMenuTagBinding
import com.example.ourmenu.databinding.TagCustomBinding
import com.example.ourmenu.databinding.TagDefaultBinding
import com.example.ourmenu.util.Utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class AddMenuTagFragment : Fragment() {

    lateinit var binding: FragmentAddMenuTagBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ScrollView> //바텀시트 상태 나타내는 변수
    var bottomSheetIcon = AddMenuBottomSheetIcon(this, 0)
    var bottomSheetIconStart = 0 //바텀시트 상태 저장. 다이얼로그로 작성된 바텀시트는 매번 새로 생성되어서 상태를 따로 저장해주어야한다.
    var bottomSheetTagAdded = ArrayList<View>()
    var bottomSheetTagRemoved = ArrayList<View>()
    var bottomSheetTagAddedbs = ArrayList<View>()
    var bottomSheetTagRemovedbs = ArrayList<View>()
    val totalSTagBS = ArrayList<View>()
    var totalSTag = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMenuTagBinding.inflate(inflater, container, false)
        binding.clAddMenuTagTag.requestLayout()
        initBottomSheet()
        binding.flAddMenuChooseTag.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED //미리 설정한 바텀시트 고정 높이만큼 올라온다
            setBlur()
        }
        binding.flAddMenuTagIcon.setOnClickListener {
            //매번 객체를 새로 생성해주지 않으면 이미 dismiss된 다이얼로그를 보여주려한다고 오류난다.
            bottomSheetIcon = AddMenuBottomSheetIcon(this, bottomSheetIconStart)
            bottomSheetIcon.show(parentFragmentManager, bottomSheetIcon.tag)
            setBlur()
        }

        //뒤로 가기 버튼
        binding.ivAddMenuTagReturn.setOnClickListener {
            handleBackPress()
        }

        var backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로가기 버튼 클릭 시의 동작을 처리합니다.
                // 예를 들어, 바텀 시트를 닫거나 다른 작업을 수행할 수 있습니다.
                handleBackPress()
            }
        }

        // 현재 프래그먼트가 활성화되었을 때 뒤로가기 버튼 이벤트를 처리하도록 콜백을 추가합니다.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        return binding.root
    }
    fun handleBackPress(){
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            clearBlur()
            closeKeyboard()
            false
        }else{
            parentFragmentManager.popBackStack()
            requireActivity().currentFocus?.clearFocus()
        }
    }
    //기본 태그 설정
    fun initDefaultTag(string: String): TagDefaultBinding {
        var tag = TagDefaultBinding.inflate(layoutInflater)
        tag.tvTagDefaultTag.setText(string)
        return tag
    }

    //커스텀 태그 설정
    fun initCustomTag(string: String): TagCustomBinding {
        var tag = TagCustomBinding.inflate(layoutInflater)
        tag.tvTagDefaultTag.setText(string)
        return tag
    }

    //태그를 바탕에서 제거
    fun removeTagFromRoot(view: View) {
        binding.clAddMenuTagTag.removeView(view)
        totalSTag.remove(view)
        if (binding.clAddMenuTagTag.childCount == 0) {
            binding.clAddMenuTagTag.visibility = View.GONE
        }
        binding.clAddMenuTagTag.requestLayout() //뷰 새로고침
    }

    //태그를 바텀시트에서 제거
    fun removeTagFromSheet(view: View) {
        binding.bsAddMenuTag.cgAmbstSelectedTag.removeView(view)
        totalSTagBS.remove(view)
        if (binding.bsAddMenuTag.cgAmbstSelectedTag.childCount == 0) {
            binding.bsAddMenuTag.cgAmbstSelectedTag.visibility = View.GONE
        }
        binding.bsAddMenuTag.cgAmbstSelectedTag.requestLayout()
    }

    fun selected(v1: View, v2: TextView, v3: ImageView) {
        v3.setColorFilter(resources.getColor(R.color.Neutral_White))
        v1.setBackgroundResource(R.drawable.btn_bg_12_p500)
        v2.setTextColor(resources.getColor(R.color.Neutral_White))
    }

    fun unselected(v1: View, v2: TextView, v3: ImageView) {
        v3.setColorFilter(resources.getColor(R.color.Neutral_900))
        v1.setBackgroundResource(R.drawable.tag_selectable)
        v2.setTextColor(resources.getColor(R.color.Neutral_900))
    }

    //기본 태그를 추가
    fun addDefaultTag(string: String, v1: View, v2: TextView, v3: ImageView) {
        if (isOverTwelve()){
            showToast(requireContext(),R.drawable.ic_error,"태그는 최대 12개까지 등록할 수 있어요.")
            return
        }
        selected(v1, v2, v3)
        var rootTag = initDefaultTag(string)
        totalSTagBS.add(v1)
        totalSTag.add(rootTag.root)
        rootTag.ivTagDelete.setOnClickListener {
            removeTagFromRoot(rootTag.root)
            removeTagFromSheet(v1)
            unselected(v1, v2, v3)
            v1.setOnClickListener { addDefaultTag(string, v1, v2, v3) }
        }
        v1.setOnClickListener {
            unselected(v1, v2, v3)
            bottomSheetTagRemovedbs.add(v1)
            bottomSheetTagRemoved.add(rootTag.root)
            bottomSheetTagAddedbs.remove(v1)
            bottomSheetTagAdded.remove(rootTag.root)
            v1.setOnClickListener { addDefaultTag(string, v1, v2, v3) }
        }
        bottomSheetTagAddedbs.add(v1)
        bottomSheetTagAdded.add(rootTag.root)
    }

    //커스텀 태그를 추가
    fun addCustomTag(string: String) {
        if (isOverTwelve()){
            showToast(requireContext(),R.drawable.ic_error,"태그는 최대 12개까지 등록할 수 있어요.")
            return
        }
        binding.bsAddMenuTag.cgAmbstSelectedTag.visibility = View.VISIBLE
        var rootTag = initCustomTag(string)
        var sheetTag = initCustomTag(string)
        totalSTagBS.add(sheetTag.root)
        totalSTag.add(rootTag.root)
        rootTag.ivTagDelete.setOnClickListener {
            removeTagFromRoot(rootTag.root)
            removeTagFromSheet(sheetTag.root)
        }
        sheetTag.ivTagDelete.setOnClickListener {
            bottomSheetTagRemoved.add(rootTag.root)
            bottomSheetTagRemovedbs.add(sheetTag.root)
            bottomSheetTagAddedbs.remove(sheetTag.root)
            bottomSheetTagAdded.remove(rootTag.root)
            removeTagFromSheet(sheetTag.root)
        }
        bottomSheetTagAdded.add(rootTag.root)
        bottomSheetTagAddedbs.add(sheetTag.root)
        binding.bsAddMenuTag.cgAmbstSelectedTag.addView(sheetTag.root)
        binding.bsAddMenuTag.cgAmbstSelectedTag.requestLayout()
    }

    //바텀시트 결과 반영(적용하기 이외 작동에는 전부 취소)
    fun updateBackgroundTag() {
        for (i in bottomSheetTagAdded) {
            binding.clAddMenuTagTag.addView(i)
            totalSTag.add(i)
        }
        for (i in bottomSheetTagRemoved) {
            binding.clAddMenuTagTag.removeView(i)
            totalSTag.remove(i)
        }
        totalSTagBS -= bottomSheetTagRemovedbs.clone() as ArrayList<View>
        bottomSheetTagRemoved.clear()
        bottomSheetTagRemovedbs.clear()
        bottomSheetTagAdded.clear()
        bottomSheetTagAddedbs.clear()
        binding.clAddMenuTagTag.requestLayout()
    }

    //변경 취소시 바텀시트에서의 변경사항 초기화
    fun resetBSTag() {
        var bstabs = bottomSheetTagAddedbs.clone() as ArrayList<View>
        var bstrbs = bottomSheetTagRemovedbs.clone() as ArrayList<View>
        var bstr = bottomSheetTagRemoved.clone() as ArrayList<View>
        var bsta = bottomSheetTagAdded.clone() as ArrayList<View>

        for (i in bstabs) {
            if (i is ConstraintLayout) {
                binding.bsAddMenuTag.cgAmbstSelectedTag.removeView(i)
            } else {
                i.performClick()
            }
        }
        if (bstr.size <= binding.clAddMenuTagTag.childCount) {
            for (i in bstrbs) {
                if (i is ConstraintLayout) {
                    binding.bsAddMenuTag.cgAmbstSelectedTag.addView(i)
                } else {
                    i.performClick()
                }
            }
        }
        totalSTagBS += bstrbs.clone() as ArrayList<View>
        totalSTag += bstr.clone() as ArrayList<View>
        totalSTagBS -= bstabs.clone() as ArrayList<View>
        totalSTag -= bsta.clone() as ArrayList<View>
        bottomSheetTagAdded.clear()
        bottomSheetTagAddedbs.clear()
        bottomSheetTagRemoved.clear()
        bottomSheetTagRemovedbs.clear()
        binding.bsAddMenuTag.cgAmbstSelectedTag.requestLayout()
    }

    //자식뷰 아무것도 없을때 뷰 자체가 안보이도록 함
    fun showCG() {
        if (binding.bsAddMenuTag.cgAmbstSelectedTag.childCount == 0) {
            binding.bsAddMenuTag.cgAmbstSelectedTag.visibility = GONE
        } else {
            binding.bsAddMenuTag.cgAmbstSelectedTag.visibility = VISIBLE
        }
        if (binding.clAddMenuTagTag.childCount == 0) {
            binding.clAddMenuTagTag.visibility = GONE
        } else binding.clAddMenuTagTag.visibility = VISIBLE
        binding.clAddMenuTagTag.requestLayout()
        binding.bsAddMenuTag.cgAmbstSelectedTag.requestLayout()
    }

    //화면 블러처리
    fun setBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.clAddMenuTag.setRenderEffect(RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP))
            binding.clAddMenuBlur.alpha = 0.2f
        }
    }

    //화면 블러 해제
    public fun clearBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.clAddMenuTag.setRenderEffect(null)
        }
        binding.clAddMenuBlur.alpha = 0f
        binding.clAddMenuTag.invalidate()
    }

    //바텀시트 이벤트 및 레이아웃 초기화
    fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bsAddMenuTag.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //아래처럼 하지 않으면 모서리모양이 제대로 적용되지 않았음.
        val shapeAppearanceModel = ShapeAppearanceModel.Builder()
            .setAllCornerSizes(20f) // 모서리 반지름 설정
            .build()
        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        shapeDrawable.setTint(resources.getColor(R.color.Neutral_White))
        binding.bsAddMenuTag.root.background = shapeDrawable

        //태그 직접 입력 창 눌렀을때 입력 버튼 활성화.
        binding.bsAddMenuTag.etAmbstEnterTag.onFocusChangeListener = (object : OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus) {

                } else {
                    binding.bsAddMenuTag.tvAmbstAddTag.visibility = View.INVISIBLE
                }
            }

        })
        binding.bsAddMenuTag.etAmbstEnterTag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.bsAddMenuTag.tvAmbstAddTag.visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 10 && start<10&&before<=10){
                    showToast(requireContext(),R.drawable.ic_error,"태그는 10글자가 넘을 수 없어요.")
                    binding.bsAddMenuTag.etAmbstEnterTag.setBackgroundResource(R.drawable.edittext_bg_error)
                    binding.bsAddMenuTag.etAmbstEnterTag.setPadding(28,10,28,10)
                    binding.bsAddMenuTag.btnAmbstConfirm.visibility = INVISIBLE
                }else if (s?.length!!>10){
                    binding.bsAddMenuTag.etAmbstEnterTag.setBackgroundResource(R.drawable.edittext_bg_error)
                    binding.bsAddMenuTag.etAmbstEnterTag.setPadding(28,10,28,10)
                    binding.bsAddMenuTag.btnAmbstConfirm.visibility = INVISIBLE
                }else if (count>before-10&&before>10){
                    binding.bsAddMenuTag.etAmbstEnterTag.setBackgroundResource(R.drawable.edittext_bg_default)
                    binding.bsAddMenuTag.etAmbstEnterTag.setPadding(28,10,28,10)
                    binding.bsAddMenuTag.btnAmbstConfirm.visibility = VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //입력 버튼 누르면 커스텀 태그 추가
        binding.bsAddMenuTag.tvAmbstAddTag.setOnClickListener {
            if (!binding.bsAddMenuTag.etAmbstEnterTag.text.isNullOrEmpty()) {
                addCustomTag(binding.bsAddMenuTag.etAmbstEnterTag.text.toString())
            }
        }

        //리셋 버튼 누르면 태그 전부 사라짐
        binding.bsAddMenuTag.btnAmbstReset.setOnClickListener {
            resetBSTag()
            val total = totalSTag.clone() as ArrayList<View>
            val totalBS = totalSTagBS.clone() as ArrayList<View>
            for (i in total) {
                bottomSheetTagRemoved.add(i)
                bottomSheetTagAdded.remove(i)
            }
            for (i in totalBS) {
                if (i is ConstraintLayout) {
                    removeTagFromSheet(i)
                    bottomSheetTagRemovedbs.add(i)
                    bottomSheetTagAddedbs.remove(i)
                } else {
                    i.performClick()
                }
            }
            showCG()
        }

        //확인 버튼 누르면 태그 적용된채로 바텀시트 내려감
        binding.bsAddMenuTag.btnAmbstConfirm.setOnClickListener {
            updateBackgroundTag()
            showCG()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            clearBlur()
        }

        //바텀시트가 최대 크기일때는 드래그 안됨(스크롤만됨)
        //바텀시트가 중간 크기일때는 드래그만 됨(스크롤 안됨) 됨
        //바텀시트 가려질시 스크롤 및 기타 초기화
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheetBehavior.isDraggable = false
                        //스크롤 풀기
                        binding.bsAddMenuTag.root.setOnTouchListener(null)
                        binding.bsAddMenuTag.root.setOnClickListener {
                            requireActivity().currentFocus?.clearFocus()
                            closeKeyboard()
                        }
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.isDraggable = true
                        //스크롤 막기
                        binding.bsAddMenuTag.root.setOnTouchListener { v, event -> true }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.bsAddMenuTag.root.scrollTo(0, 0)
                        binding.bsAddMenuTag.etAmbstEnterTag.text.clear()
                        binding.bsAddMenuTag.etAmbstEnterTag.clearFocus()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        //바텀시트 상단 회색 가로버튼 클릭시 바텀시트 바로 올라감, 사라짐
        binding.bsAddMenuTag.ivAmbstBtn.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                clearBlur()
                closeKeyboard()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        //바탕 클릭 시 바텀시트 들어감
        binding.root.setOnClickListener {
            resetBSTag()
            showCG()
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                clearBlur()
            }
            closeKeyboard()
            requireActivity().currentFocus?.clearFocus()
        }

        //바텀시트에서 선택 가능한 태그 눌렀을때 바텀시트에서 태그 추가
        binding.bsAddMenuTag.flAmbstRice.setOnClickListener {
            addDefaultTag(
                binding.bsAddMenuTag.tvAmbstRice.text.toString(),
                binding.bsAddMenuTag.flAmbstRice,
                binding.bsAddMenuTag.tvAmbstRice,
                binding.bsAddMenuTag.ivAmbstRice
            )
        }
        binding.bsAddMenuTag.flAmbstBread.setOnClickListener {
            addDefaultTag(
                binding.bsAddMenuTag.tvAmbstBread.text.toString(),
                binding.bsAddMenuTag.flAmbstBread,
                binding.bsAddMenuTag.tvAmbstBread,
                binding.bsAddMenuTag.ivAmbstBread
            )
        }
        binding.bsAddMenuTag.flAmbstNoodle.setOnClickListener {
            addDefaultTag(
                binding.bsAddMenuTag.tvAmbstNoodle.text.toString(),
                binding.bsAddMenuTag.flAmbstNoodle,
                binding.bsAddMenuTag.tvAmbstNoodle,
                binding.bsAddMenuTag.ivAmbstNoodle
            )
        }
        binding.bsAddMenuTag.flAmbstMeat.setOnClickListener {
            addDefaultTag(
                binding.bsAddMenuTag.tvAmbstMeat.text.toString(),
                binding.bsAddMenuTag.flAmbstMeat,
                binding.bsAddMenuTag.tvAmbstMeat,
                binding.bsAddMenuTag.ivAmbstMeat
            )
        }
        //todo 남은 선택 가능한 태그도 clickListener 설정 필요
    }


    fun closeKeyboard() {
        val inputManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun isOverTen(): Boolean {
        if((binding.bsAddMenuTag.etAmbstEnterTag.textSize) > 10){
            return true
        }else return false
    }

    fun isOverTwelve(): Boolean {
        if((totalSTagBS - bottomSheetTagRemovedbs.clone() as ArrayList<View>).size >= 12){
            return true
        }else return false
    }
}
