package com.example.capacita_projeto_final.features.visit.domain

enum class ReadingError { NotANumber, BelowPreviousReading }

sealed interface ReadingValidation {
    data class Valid(val reading: Int) : ReadingValidation
    data class Invalid(val reason: ReadingError) : ReadingValidation
}

fun validateReading(input: String, previousReading: Int): ReadingValidation {
    val reading = input.trim().toIntOrNull()
        ?: return ReadingValidation.Invalid(ReadingError.NotANumber)

    if (reading < previousReading) {
        return ReadingValidation.Invalid(ReadingError.BelowPreviousReading)
    }

    return ReadingValidation.Valid(reading)
}
