package com.jetbrains.speechdetection

expect fun platformName(): String

expect class Mfcc(params: Parameters) {
    fun compute(inFrame: FloatArray): FloatArray
}

expect val String.bytes: ByteArray
