import org.zeromq.ZContext
import org.zeromq.ZMQ
import org.zeromq.ZMsg
import kotlinx.coroutines.*

class ZMQService(private val uiCallback: (String) -> Unit) {

    private val context = ZContext()
    private val socket = context.createSocket(ZMQ.SUB)
    private val heartbeatInterval = 5000L // 5 seconds

    init {
        try {
            socket.connect("tcp://yourserver:5555")
            socket.subscribe("")

            // Start listening for messages
            CoroutineScope(Dispatchers.IO).launch {
                listenForMessages()
            }

            // Start sending heartbeats
            CoroutineScope(Dispatchers.IO).launch {
                sendHeartbeats()
            }
        } catch (e: Exception) {
            uiCallback("Error connecting to server: ${e.message}")
        }
    }

    private suspend fun listenForMessages() {
        while (true) {
            try {
                val msg = ZMsg.recvMsg(socket)
                msg?.let {
                    val message = it.popString()
                    withContext(Dispatchers.Main) {
                        uiCallback(message)
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        uiCallback("Error receiving message")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiCallback("Error in message listener: ${e.message}")
                }
            }
        }
    }

    private suspend fun sendHeartbeats() {
        while (true) {
            delay(heartbeatInterval)
            try {
                val heartbeat = ZMsg()
                heartbeat.addString("HEARTBEAT")
                heartbeat.send(socket)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiCallback("Error sending heartbeat: ${e.message}")
                }
            }
        }
    }

    fun sendMessage(topic: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = ZMsg()
                msg.addString(topic)
                msg.addString(message)
                msg.send(socket)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiCallback("Error sending message: ${e.message}")
                }
            }
        }
    }

    fun close() {
        try {
            socket.close()
            context.close()
        } catch (e: Exception) {
            uiCallback("Error closing socket: ${e.message}")
        }
    }
}
