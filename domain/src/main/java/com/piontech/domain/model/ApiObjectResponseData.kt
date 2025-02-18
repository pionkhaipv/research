package com.piontech.domain.model

data class ApiObjectResponseData<T>(
    var message: String,
    var dataResponse: T,
    var status: Int
)
