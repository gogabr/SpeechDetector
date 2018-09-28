package com.jetbrains.speechdetection.steps

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

// Trivial CMVN, works in batch mode
fun CoroutineScope.cmvnBatch(inChannel: ReceiveChannel<FloatArray>) = produce {
    val batch = mutableListOf<FloatArray>()
    for (frame in inChannel) {
        batch.add(frame)
    }
    if (batch.size == 0) return@produce
    val frameSize = batch.first().size
    val averages = FloatArray(frameSize)
    for (i in averages.indices) {
        averages[i] = batch.map { it[i] }.average().toFloat()
    }
    batch.forEach {inFrame ->
        val outFrame = FloatArray(frameSize)
        for (i in inFrame.indices) { outFrame[i] = inFrame[i] - averages[i] }
        send(outFrame)
    }
    close()
}