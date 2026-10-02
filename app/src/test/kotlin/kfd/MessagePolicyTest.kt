package kfd

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessagePolicyTest {
    @Test
    fun acceptsShortMessage() {
        assertTrue(canSendMessage("Привет"))
    }

    @Test
    fun declineEmptyMessage() {
        assertFalse(canSendMessage(""))
    }

    @Test
    fun declineBlankMessage() {
        assertFalse(canSendMessage("     "))
        assertFalse(canSendMessage("\n"))
        assertFalse(canSendMessage("\t"))
    }

    @Test
    fun declineNullMessage() {
        assertFalse(canSendMessage(null))
    }

    @Test
    fun declineEmptyValues() {
        assertFalse(canSendMessage())
    }

    @Test
    fun declineInvalidMax() {
        assertFalse(canSendMessage("Привет", 0))
        assertFalse(canSendMessage("Привет", -2))
    }

    @Test
    fun declineLongMessage() {
        assertFalse(canSendMessage("Привет", 2))
    }

}