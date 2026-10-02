package kfd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DisplayNameTest {
    @Test
    fun preservesName() {
        assertEquals("Ann", displayName("Ann"))
    }

    @Test
    fun handlesNull() {
        assertEquals("Guest", displayName(null))
    }

    @Test
    fun handlesBlank() {
        assertEquals("Guest", displayName(" "))
        assertEquals("Guest", displayName("\t"))
        assertEquals("Guest", displayName("\n"))
        assertEquals("Guest", displayName("\r"))
        assertEquals("Guest", displayName("\t \n\r"))
    }

    @Test
    fun handlesEmptyInput(){
        assertEquals("Guest", displayName())
    }

    @Test
    fun handlesUntrimmedName(){
        assertEquals("Ann", displayName("      Ann       "))
    }
}
