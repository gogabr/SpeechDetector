package com.jetbrains.speechdetection.data

import java.nio.ByteBuffer
import java.nio.ByteOrder

actual open class KaldiReaderImpl actual constructor(bytes: ByteArray) {
    var offset = 0

    val buffer = ByteBuffer.wrap(bytes).apply {
        order(ByteOrder.LITTLE_ENDIAN)
        // read the binary header
        assert(getShort().toInt() == 0x4200)
        offset += 2 // sizeof(Short)
//        /**/ println("size ${bytes.size}")
    }

    actual fun readByte(): Byte? {
        val array = buffer.array()
        if (offset >= array.size) return null
        return array[offset++]
    }

    actual fun readUntil(c: Char): String? {
        val b = c.toByte()
        val byteArray = buffer.array()
        val ix = byteArray.sliceArray(offset until byteArray.size).indexOf(b)
        if (ix == -1) return null
        return String(byteArray.sliceArray(offset until (offset + ix))).also {
            offset += ix + 1
        }
    }

    actual fun readRawInt(): Int  {
//        /**/ println("get raw int at $offset")
        return buffer.getInt(offset).also { offset += 4 }
    }

    actual fun readRawFloat(): Float {
//        /**/ println("get raw float at $offset")
        return buffer.getFloat(offset).also { offset += 4 }
    }
}