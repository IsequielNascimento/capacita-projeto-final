package com.example.capacita_projeto_final.features.visit.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingValidatorTest {
    @Test
    fun rejectsNonNumericReading() {
        assertTrue(validateReading("", 100) is ReadingValidation.Invalid)
    }

    @Test
    fun rejectsReadingBelowPreviousValue() {
        assertTrue(validateReading("99", 100) is ReadingValidation.Invalid)
    }

    @Test
    fun acceptsReadingEqualOrAbovePreviousValue() {
        assertEquals(ReadingValidation.Valid(120), validateReading("120", 100))
    }
}
