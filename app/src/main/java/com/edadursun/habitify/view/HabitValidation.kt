package com.edadursun.habitify.view


//Alt sınıfları sınırlı ve compile-time’da bilinen bir class türüdür.
//Kimlerin bu class’ı extend edebileceği bellidir
//Dışarıdan rastgele subclass oluşturulamaz
//when kullanırken exhaustiveness (tüm durumları kapsama) garantisi verir

// Hangi alan boş bilgisini taşır
sealed class HabitValidation {
    object HabitTitleEmpty : HabitValidation()
    object TargetValueEmpty : HabitValidation()
    object ReminderMessageEmpty : HabitValidation()
    object Success : HabitValidation()
}