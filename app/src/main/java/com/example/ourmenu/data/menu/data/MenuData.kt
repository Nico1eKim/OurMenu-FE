package com.example.ourmenu.data.menu.data


data class MenuData(
    val groupId: Int,
    val menuTitle: String,
    val placeTitle: String,
    val placeAddress: String,
    val menuPrice: Int,
    val menuImgUrl: String
)

data class MenuInfoData(
    val groupId: Int,
    val menuTitle: String,
    val menuPrice: Int,
    val menuMemoTitle: String,
    val menuMemo: String,
    val menuIconType: String,
    val menuTags: ArrayList<MenuTag>,
    val menuImages: ArrayList<MenuImage>,
    val menuFolders: ArrayList<MenuFolderChip>
)

data class MenuTag(
    val tagTitle: String,
    val custom: Boolean
)

data class MenuImage(
    val menuImgUrl: String
)

data class MenuFolderChip(
    val menuFolderTitle: String,
    val menuFolderIcon: String
)
