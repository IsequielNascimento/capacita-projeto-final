package com.example.capacita_projeto_final.features.visit.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingValidatorTest {
    @Test
    fun rejectsNonNumericReading() {
        assertEquals(
            ReadingValidation.Invalid(ReadingError.NotANumber),
            validateReading("", 100),
        )
    }

    @Test
    fun rejectsReadingBelowPreviousValue() {
        assertEquals(
            ReadingValidation.Invalid(ReadingError.BelowPreviousReading),
            validateReading("99", 100),
        )
    }

    @Test
    fun acceptsReadingEqualOrAbovePreviousValue() {
        assertEquals(ReadingValidation.Valid(120), validateReading("120", 100))
    }
}
