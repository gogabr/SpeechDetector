package com.jetbrains.speechdetection

// Most of these should be configurable
class Parameters {
    
    val sampleRate = 8000
    val frameSizeMs = 25
    val frameShiftMs = 10
    
    val dither = 0.1f

    val numMelBins = 40
    val numCeps = 40

    val frameSize = (sampleRate * frameSizeMs / 1000)
    val frameShift = (sampleRate * frameShiftMs / 1000)

    val preemphCoeff = 0.97f

    val randomSeed = 12345678
}