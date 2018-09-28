package com.jetbrains.speechdetection.data

expect open class KaldiReaderImpl(bytes: ByteArray) {
    fun readUntil(c: Char): String?
    fun readByte(): Byte?
    fun readRawInt(): Int
    fun readRawFloat(): Float
}

class KaldiReader(bytes: ByteArray) : KaldiReaderImpl(bytes) {
    class InputFormatException(s: String) : Exception(s)

    fun readToken(): String? = readUntil(' ')
    fun readLine(): String? = readUntil('\n')

    fun expectToken(s: String) {
        val tok = readToken() ?: throw InputFormatException("Unexpected EOF")
        if (s != tok) throw InputFormatException("Expected token $s, got $tok")
    }

    fun expectByte(b: Byte) {
        val inByte = readByte() ?: throw InputFormatException("Unexpected EOF")
        if (inByte != b) throw InputFormatException("Expected byte $b, got $inByte")
    }

    fun readMatrix(): Array<FloatArray> {
        expectToken("FM")
        expectByte(4)
        val nRows = readRawInt()
        expectByte(4)
        val nCols = readRawInt()
        println("size ${nRows}x${nCols}")
        // TODO: This relies on the order of map()  application
        return (0 until nRows).map {
            (0 until nCols).map { readRawFloat() }.toFloatArray()
        }.toTypedArray()
    }
}