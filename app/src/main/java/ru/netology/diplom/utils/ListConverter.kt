package ru.netology.diplom.utils

import androidx.room.TypeConverter

class ListConverter {

    @TypeConverter
    fun fromListLongToString(longList: List<Long>): String = longList.toString()

    @TypeConverter
    fun toListLongFromString(stringList: String): List<Long> {
        val result = ArrayList<Long>()
        val split = stringList.replace("[", "")
            .replace("]", "")
            .replace(" ", "")
            .split(",")
        for (n in split) {
            try {
                result.add(n.toLong())
            } catch (e: Exception) {}
        }
     return result
    }
}