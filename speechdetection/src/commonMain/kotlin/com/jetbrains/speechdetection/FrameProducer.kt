package com.jetbrains.speechdetection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

fun CoroutineScope.produceFrames(inChannel: Channel<ShortArray>, params: Parameters) = produce {
    val frameSize = params.frameSize
    val frameShift = params.frameShift
    val featureComputer = FeatureComputer(params)
    var buf = shortArrayOf()

    for (newData in inChannel) {
        buf += newData
        while (buf.size >= frameSize) {
            val raw = buf.copyOfRange(0, frameSize)
//            println(raw.map { it.toString() }.joinToString(separator = " ", prefix = "raw: "))
            send(featureComputer.compute(raw))
            buf = buf.copyOfRange(frameShift, buf.size)
        }
    }
}