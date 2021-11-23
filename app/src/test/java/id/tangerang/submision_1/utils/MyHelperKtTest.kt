package id.tangerang.submision_1.utils

import junit.framework.TestCase
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyHelperKtTest : TestCase() {

    fun testMyNumberFormat() {
        assertEquals("10.000", myNumberFormat("10000"))
    }

    fun testGetCurrentDate() {
        assertEquals("2021-09-24 13:39:00", getCurrentDate())
    }
}