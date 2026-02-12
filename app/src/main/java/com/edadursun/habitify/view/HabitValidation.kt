package com.edadursun.habitify.view

import android.widget.Toast

// Hangi alan boş bilgisini taşır
sealed class ToastMessage {
    object Success : ToastMessage()
    object HabitTitleEmpty : ToastMessage()
    object TargetValueEmpty : ToastMessage()
    object ReminderMessageEmpty : ToastMessage()
}