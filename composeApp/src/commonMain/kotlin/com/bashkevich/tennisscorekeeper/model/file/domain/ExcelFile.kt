package com.bashkevich.tennisscorekeeper.model.file.domain

data class ExcelFile(
    val name: String,
    val content: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ExcelFile

        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }

}

val EMPTY_EXCEL_FILE = ExcelFile(name = "", content = ByteArray(0))
