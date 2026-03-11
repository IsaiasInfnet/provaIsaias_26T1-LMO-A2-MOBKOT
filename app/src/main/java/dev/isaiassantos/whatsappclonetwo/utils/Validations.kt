package dev.isaiassantos.whatsappclonetwo.utils

import com.google.android.material.textfield.TextInputEditText

class Validations {
    companion object {
        @JvmStatic
        fun validateUserInputs(
            emailInput: TextInputEditText,
            passwordInput: TextInputEditText,
            editTextUsername: TextInputEditText? = null,
            editTextPhone: TextInputEditText? = null
        ) : Boolean {
            return true
        }
    }
}