package com.edadursun.habitify.model

//RecyclerView / LazyColumn / Adapter gibi yapılarda her satır bir “veri objesi” ile temsil edilir.
//Uida göstereceğim her bir günü tek bir model nesnesi olarak temsil eder
data class DayItem(
    val dayName:String,
    val dayNumber:String
)
