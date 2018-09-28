package com.jetbrains.speechdetection

class FeatureComputer(val params: Parameters) {
    val mfcc = Mfcc(params)

    fun compute(inFrame: ShortArray): FloatArray {
        val frameFloat = FloatArray(inFrame.size)
        inFrame.forEachIndexed { i, elem ->
            frameFloat[i] = elem.toFloat()
        }
        return mfcc.compute(frameFloat)
    }
}