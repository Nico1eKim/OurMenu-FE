package com.example.ourmenu.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.ourmenu.databinding.ToastMessageBgBinding

object Utils {
    fun dpToPx(
        context: Context,
        dp: Int,
    ): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun isValidPassword(password: String): Boolean {
        // 영문, 숫자 포함 8자 이상인지 확인
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
        return password.matches(Regex(passwordPattern))
    }

    fun showToast(
        context: Context,
        icon: Int,
        message: String,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val toastBinding: ToastMessageBgBinding = ToastMessageBgBinding.inflate(layoutInflater)

        toastBinding.ivToastMessage.setImageResource(icon)
        toastBinding.tvToastMessage.text = message

        val toast = Toast(context)
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_SHORT

        // Toast 메시지를 화면 상단으로부터 78dp 떨어진 위치에 표시
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, dpToPx(context, 128))

        toast.show()
    }

    fun hideKeyboard(
        context: Context,
        view: View,
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
