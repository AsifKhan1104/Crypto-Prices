package com.crypto.prices.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SpinData(val email: String? = null, val coins: Double? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}