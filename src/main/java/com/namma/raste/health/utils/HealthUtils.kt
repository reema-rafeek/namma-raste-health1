package com.namma.raste.health.utils

object HealthUtils {
    /**
     * Logic to calculate road health based on reported damages.
     * Starts at 100% and decreases based on severity.
     */
    fun calculateScore(potholes: Int, cracks: Int, waterLogging: Int): Int {
        val deduction = (potholes * 15) + (cracks * 5) + (waterLogging * 10)
        return (100 - deduction).coerceAtLeast(0)
    }

    fun getStatusFromScore(score: Int): String {
        return when {
            score >= 80 -> "Healthy"
            score >= 50 -> "Warning"
            else -> "Damaged"
        }
    }
}
