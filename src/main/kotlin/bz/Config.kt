package bz

import androidx.compose.runtime.toMutableStateList
import java.io.File
import java.io.FileInputStream
import java.util.Properties

object Config {

    private var props: Properties = Properties()
    private lateinit var file: File

    fun load(fileName: String) {
        file = File(fileName)
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

    fun getAll(): MutableSet<MutableMap.MutableEntry<Any, Any>> {
        return props.entries
    }

    fun getSafeDictionary(): MutableList<MutableMap.MutableEntry<Any, Any>> {
        return props.entries.filter { !it.key.toString().startsWith("_") }.toMutableStateList()
    }

    fun setValue(key: Any, value: Any) {
        props[key] = value
        props.save(file.outputStream(), "Save")
    }

    fun save(dictionary: MutableList<MutableMap.MutableEntry<Any, Any>>) {
        for ((key, value) in dictionary) {
            setValue(key, value)
        }
    }

    fun getBoolean(key: String): Boolean {
        return getString(key).toBoolean()
    }

}