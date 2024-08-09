package com.example.ourmenu.menu.iteminterface

interface MenuFolderItemClickListener {
    // 메뉴판 클릭
    fun onMenuClick(menuFolderId: Int)
    // 수정
    fun onEditClick()
    // 삭제
    fun onDeleteClick()

}
