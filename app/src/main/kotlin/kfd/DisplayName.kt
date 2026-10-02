package kfd

fun displayName(name: String? = "Guest"): String {
    val trimmedName = name?.trim() ?: "Guest"
    return if (trimmedName != "") trimmedName else "Guest"
}

fun main(){
    println(displayName(null))
    println(displayName())
    println(displayName(" "))
    println(displayName(""))
    println(displayName("Ann"))
    println(displayName("           Ann         \n"))
}