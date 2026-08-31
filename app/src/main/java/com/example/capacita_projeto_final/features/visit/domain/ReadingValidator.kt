package com.example.capacita_projeto_final.features.visit.domain

sealed interface ReadingValidation {
    data class Valid(val reading: Int) : ReadingValidation
    data class Invalid(val message: String) : ReadingValidation
}

fun validateReading(input: String, previousReading: Int): ReadingValidation {
    val reading = input.trim().toIntOrNull()
        ?: return ReadingValidation.Invalid("Informe uma leitura numérica válida.")

    if (reading < previousReading) {
        return ReadingValidation.Invalid("A leitura atual não pode ser menor que a anterior.")
    }

    return ReadingValidation.Valid(reading)
}
