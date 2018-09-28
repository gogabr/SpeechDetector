package com.jetbrains.speechdetection.nnet3

import com.jetbrains.speechdetection.data.KaldiReader

class Nnet3 {
    companion object {
        fun read(bytes: ByteArray) {
            val reader = KaldiReader(bytes)
            while (true) {
                val line = reader.readLine()!!
                if (line.isEmpty()) break
                println(line)
            }
        }
    }
}

private class Nnet3Header(val dict: Map<String, Node>) {
    sealed class Node {
        class InputNode(val dim: Int) : Node()
        class OutputNode(val input: String) : Node()
        class ComponentNode(val componentName: String, val input: String) : Node()
        class OffsetNode(val input: String, val shift: Int) : Node()
        class AppendNode(val inputs: List<String>) : Node()
        class RoundNode(val input: String, val modulus: Int) : Node()
    }

    companion object {
        fun read(reader: KaldiReader): Nnet3Header {
            val namer = NameGenerator()
            val dict = mutableMapOf<String, Node>()
            while (true) {
                val line = reader.readLine()!!
                if (line.isEmpty()) break
                parseLine(line, namer).forEach { (name, node) -> dict[name] = node }
            }
            return Nnet3Header(dict)
        }

        fun parseLine(line: String, namer: NameGenerator): Set<Pair<String, Node>> {
            val fields = line.splitWithParentheses().map { it.trim() }
            if (fields.isEmpty()) return emptySet()
            val nodeTypeName = fields[0]
            val fieldMap = fields.drop(1)
                    .map { it.split('=', limit = 2) }
                    .map { it[0] to it[1] }
                    .toMap()
            val nodeName = fieldMap["name"]!!
            when (nodeTypeName) {
                "input-node" -> return setOf(nodeName to Node.InputNode(fieldMap["dim"]!!.toInt()))
                "output-node" -> {
                    val inputStr = fieldMap["input"]!!
                    return setOf(nodeName to Node.OutputNode(inputStr))
                }
            }
            /**/return emptySet()
        }

    }
}

private class NameGenerator {
    var count = 0

    fun next(): String = "__tmp__" + (count++).toString()
}

// Trivial handmade parser, inefficient but simple
private fun String.splitWithParentheses(delimiter: Char = ' '): List<String> {
    var count = 0
    val bigAccum = mutableListOf<String>()
    var smallAccum = mutableListOf<Char>()
    forEach {
        when {
            it == delimiter && count == 0 -> if (smallAccum.isNotEmpty()) {
                bigAccum.add(String(smallAccum.toCharArray()))
                smallAccum = mutableListOf<Char>()
            }
            it == '(' -> {
                count++
                smallAccum.add(it)
            }
            it == ')' -> {
                count--
                smallAccum.add(it)
            }
            else -> smallAccum.add(it)
        }
    }
    if (smallAccum.isNotEmpty()) {
        bigAccum.add(String(smallAccum.toCharArray()))
    }
    return bigAccum
}