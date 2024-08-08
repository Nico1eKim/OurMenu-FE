package com.example.ourmenu.data.menuFolder.data

import com.google.gson.annotations.SerializedName


// /menuFolder
data class MenuFolderData(
    val menuCount: Int,
    val menuFolderIcon: String,
    val menuFolderImgUrl: String,
    val menuFolderPriority: Int,
    val menuFolderTitle: String
)

