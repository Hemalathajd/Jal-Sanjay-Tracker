package com.jalsanchay.tracker.models

import com.google.firebase.Timestamp

// ✅ USER PROFILE
data class UserProfile(
    val userId: String = "",
    val roofArea: Double = 0.0,
    val tankCapacity: Double = 0.0
)

// ✅ RAINFALL ENTRY (FIXED)
data class RainfallEntry(
    val userId: String = "",
    val rainfall: Double = 0.0,
    val waterCollected: Double = 0.0,
    val date: Timestamp? = null   // 🔥 FIX HERE
)