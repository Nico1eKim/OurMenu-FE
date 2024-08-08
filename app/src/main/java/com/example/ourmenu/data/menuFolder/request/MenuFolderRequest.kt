package com.example.ourmenu.data.menuFolder.request

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class MenuFolderRequest(
    val menuFolderImgUrl: Uri? = null,
    val menuFolderTitle: String,
    val menuFolderIcon: String
)
