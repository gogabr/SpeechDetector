package com.jetbrains.speechdetection

import edu.mit.media.funf.math.FFT
import edu.mit.media.funf.math.MFCC
import edu.mit.media.funf.math.Window
import kotlin.random.Random

actual fun platformName() = "jvm"

actual class Mfcc actual constructor(val params: Parameters) {
    private val frameSize = params.frameSize

    private val frameSizeAligned = run {
        var n = 1
        while (n < frameSize) { n = n shl 1 }
        n
    }

    private val window = Window(frameSize)
    private val fft = FFT(frameSizeAligned)
    private val internalMfcc = MFCC(frameSizeAligned, params.numCeps, params.numMelBins, params.sampleRate.toFloat())

    actual fun compute(inFrame: FloatArray): FloatArray {
        assert(inFrame.size == frameSize)

//        /**/println(inFrame.map { it.toString() }.joinToString(separator = " ", prefix = "raw: "))
//        dither(inFrame, params.dither)
        subtractAverage(inFrame)
//        /**/println(inFrame.map { it.toString() }.joinToString(separator = " ", prefix = "after subtractAverage: "))
        preemphasize(inFrame, params.preemphCoeff)
//        /**/println(inFrame.map { it.toString() }.joinToString(separator = " ", prefix = "after preemph: "))
        window.applyWindow(inFrame)
//        /**/println(inFrame.map { it.toString() }.joinToString(separator = " ", prefix = "after window: "))

        val re = inFrame.copyOf(frameSizeAligned)
        val im = FloatArray(frameSizeAligned) // zeroed
        fft.fft(re, im)
//        /**/println(re.map { it.toString() }.joinToString(separator = " ", prefix = "after FFT re: "))
//        /**/println(im.map { it.toString() }.joinToString(separator = " ", prefix = "after FFT im: "))
        return internalMfcc.cepstrum(re, im)
    }

    private val random = Random(params.randomSeed)

    private fun dither(inFrame: FloatArray, coeff: Float) {
        for (i in 0 until inFrame.size) {
            inFrame[i] += coeff * (random.nextFloat() * 2.0f - 1.0f)
        }
    }

    private fun subtractAverage(inFrame: FloatArray) {
        val average = inFrame.average().toFloat()
        for (i in 0 until frameSize) {
            inFrame[i] -= average
        }
    }

    private fun preemphasize(frame: FloatArray, coeff: Float) {
        for (i in (1 until frame.size).reversed()) {
            frame[i] -= coeff * frame[i-1]
        }
        frame[0] -= coeff * frame[0]
    }
}

actual val String.bytes
    get() = toByteArray()
