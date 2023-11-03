package mobi.sevenwinds.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object TimeUtils {
    fun timeToString(date: DateTime): String {
        val formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        return date.toString(formatter)
    }
}