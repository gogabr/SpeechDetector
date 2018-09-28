import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import java.io.FileInputStream
import com.jetbrains.speechdetection.platformName
import com.jetbrains.speechdetection.produceFrames
import com.jetbrains.speechdetection.Parameters
import com.jetbrains.speechdetection.nnet3.Nnet3
import com.jetbrains.speechdetection.steps.cmvnBatch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import java.io.File

fun main(args: Array<String>) {
//    runBlocking {
//        launch(Dispatchers.Default) {
//            val BUFSIZE = 2048
//
//            println(platformName())
//            assert(args.size == 2)
//            val inStream = FileInputStream(args[0])
//            val header = ByteArray(44)
//
//            // TODO: assert is not appropriate for IO checks.
//            assert(inStream.read(header, 0, 44) == 44)
//            // TODO need better checks.
//            assert(header.copyOfRange(0, 4).contentEquals("RIFF".toByteArray()))
//
//            val rawDataChannel = Channel<ShortArray>()
//
//            val frameProducer = produceFrames(rawDataChannel, Parameters())
//            val cmvnComputer = cmvnBatch(frameProducer)
//            launch { receiver(cmvnComputer) }
//
//            val buf = ByteArray(BUFSIZE)
//            while (true) {
//                val chunkLength = inStream.read(buf, 0, BUFSIZE)
//                if (chunkLength <= 0) break
//
//                val chunkOfShorts = buf.copyOfRange(0, chunkLength).asList().chunked(2)
//                        .map { (l, h) -> ((l.toInt() and 0xff) + (h.toInt() shl 8)).toShort() }
//                        .toShortArray()
//
//                ///**/ println("on input " + chunkOfShorts.map { it.toString() }.joinToString(separator = " "))
//                rawDataChannel.send(chunkOfShorts)
//            }
//
//            println("End input")
//            inStream.close()
//            rawDataChannel.close()
//
//        }
        val nnetBytes = File(args[1]).readBytes()
        Nnet3.read(nnetBytes)
//    }
}

private suspend fun receiver(inChannel: ReceiveChannel<FloatArray>) {
    inChannel.consumeEach { frame ->
        println("CMVN " + frame.map {it.toString() }.joinToString(separator = " "))
    }
}
