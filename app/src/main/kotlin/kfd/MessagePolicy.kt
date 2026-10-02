package kfd

fun canSendMessage(
    text: String? = "",
    maxLength: Int = 140
): Boolean =  !text.isNullOrBlank() && text.length < maxLength