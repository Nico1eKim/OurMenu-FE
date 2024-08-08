package com.example.ourmenu.menu.menuFolder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.R
import com.example.ourmenu.databinding.ActivityMenuFolderDetailBinding

class MenuFolderDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuFolderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuFolderDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 전체 메뉴판 클릭인지 확인
        val isAll =
            intent.getBooleanExtra("isAll"/* true */, false)

        if (isAll) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.menu_folder_frm, MenuFolderDetailAllFragment())
                .commitAllowingStateLoss()

        } else {
            // 메뉴화면에서 수정버튼으로 클릭되면 true, 기본값이면 false
            val isEdit =
                intent.getBooleanExtra(
                    "isEdit",/* true */
                    false,
                )

            // 수정화면 인지 기본화면 인지 세팅하는 부분
            val menuFolderFragment = MenuFolderDetailFragment()
            val bundle = Bundle()
            bundle.putBoolean("isEdit", isEdit as Boolean)
            menuFolderFragment.arguments = bundle

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.menu_folder_frm, menuFolderFragment)
                .commitAllowingStateLoss()
        }
    }
}
