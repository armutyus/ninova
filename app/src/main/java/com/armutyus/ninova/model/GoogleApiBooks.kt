package com.armutyus.ninova.model

data class GoogleApiBooks(
    val items: List<GoogleBookItem>,
    val kind: String,
    val totalItems: Int
)