package com.example.ourmenu.data.menu.response

import com.example.ourmenu.data.ErrorResponse
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menu.data.MenuPlaceDetailData
import com.example.ourmenu.data.menu.data.MenuInfoData

// /menu
data class MenuArrayResponse(
    val isSuccess: Boolean,
    val response: ArrayList<MenuData>,
)

// menu (post)
data class PostMenuResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: MenuGroupId,
)

data class MenuGroupId(
    val menuGroupId: Int,
)

// menu/photo
data class PostMenuPhotoResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: String,
)

// menu/place/{placeID}
data class MenuPlaceDetailResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: ArrayList<MenuPlaceDetailData>,
)

// menu/{groupId}
data class MenuInfoResponse(
    val isSuccess: Boolean,
    val response: MenuInfoData
)
