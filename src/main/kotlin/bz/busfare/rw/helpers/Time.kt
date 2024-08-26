package bz.busfare.rw.helpers


object Time {

    const val DATE_BITMASK: Long = 0xFFFFF
    const val DATE_ONE_YEAR = (((60 * 60) * 24) * 365)

    fun getCurrentTimeSeconds(): Long {
        return System.currentTimeMillis() / 1000
    }

    fun getAddYearToCurrentTimeSeconds(): Long {
        return ((((getCurrentTimeSeconds() + DATE_ONE_YEAR) / (86400)) * 86400))
    }
}