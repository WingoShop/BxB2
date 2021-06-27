package com.nywangga.bxb

data class User(
    var name: String? = null,
    var email: String? = null,
    var pairs: MutableList<String>? = mutableListOf(),
    var currentPair: String? = ""
)