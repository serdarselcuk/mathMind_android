package com.src.mathmind.service


class ServiceResponse<T> {
    // Getters for data and errorMessage
    var data: T? = null
        private set
    var errorMessage: String? = null
        private set

    // Constructor for successful response
    constructor(data: T) {
        this.data = data
    }

    // Constructor for error response
    constructor(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    val isSuccess: Boolean
        // Method to check if response is successful
        get() = errorMessage == null
}

