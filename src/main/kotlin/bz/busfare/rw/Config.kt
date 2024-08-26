package bz.busfare.rw

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object Config {

    private var props: Properties = Properties()

    fun load(fileName: String) {
        val file = File(fileName)
        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            props.load(fileInputStream)
        } else {
            throw Exception("$fileName does not exist")
        }
    }

    fun getString(key: String): String? {
        return props.getProperty(key, null)
    }

    fun getInt(key: String): Int? {
        return getString(key)?.toInt()
    }

}