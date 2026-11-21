package kfd


fun displayName(name: String? = null): String {
    if (name == null) return "Guest"

    val trimmedName = name.trim()
    return if (trimmedName != "") trimmedName else "Guest"
}
