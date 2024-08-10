package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.user.UserImageData
import com.example.ourmenu.data.user.UserNicknameData
import com.example.ourmenu.data.user.UserPasswordData
import com.example.ourmenu.data.user.UserPatchResponse
import com.example.ourmenu.data.user.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserService {
    @PATCH("user/password")
    fun patchUserPassword(
        @Body body: UserPasswordData
    ): Call<UserPatchResponse>

    @PATCH("user/nickname")
    fun patchUserNickname(
        @Body nickname: UserNicknameData
    ): Call<UserPatchResponse>

    @PATCH("user/image")
    fun patchUserImage(
        @Body imgFile : UserImageData
    ): Call<UserPatchResponse>

    @GET("user")
    fun getUser() : Call<UserResponse>
}
