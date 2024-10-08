package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.onboarding.response.OnboardingRecommendResponse
import com.example.ourmenu.data.onboarding.response.OnboardingResponse
import com.example.ourmenu.data.onboarding.response.OnboardingStateResponse
import com.example.ourmenu.data.onboarding.response.OnboardingTagResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OnboardingService {
    @GET("onboarding")
    fun getOnboarding(): Call<OnboardingResponse>

    @GET("onboarding/recommend")
    fun getRecommend(
        @Query("questionId") questionId : Int,
        @Query("answer") answer : String
    ): Call<OnboardingRecommendResponse>

    @GET("onboarding/recommend/tag")
    fun getOnboardingTag(): Call<OnboardingTagResponse>

    @GET("onboarding/state")
    fun getOnboardingState(): Call<OnboardingStateResponse>
}
