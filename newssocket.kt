import kotlinx.coroutines.*
import org.zeromq.SocketType
import org.zeromq.ZMQ
import org.zeromq.ZMQException
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

class ZMQService(
    private val serverAddress: String,
    private val maxRetries: Int = 5,               // Max retry attempts for connection and sending messages
    private val retryDelayMillis: Long = 2000L,    // Initial retry delay
    private val maxRetryDelayMillis: Long = 60000L, // Max retry delay (exponential backoff)
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
) : CoroutineScope {

    private var context: ZMQ.Context = ZMQ.context(1)
    private var socket: ZMQ.Socket? = null
    private var heartbeatJob: Job? = null
    private var isConnected: Boolean = false

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    // Method to connect with retry mechanism
    fun connect() {
        launch {
            var attempt = 0
            while (attempt < maxRetries) {
                try {
                    socket = context.socket(SocketType.SUB).apply {
                        connect(serverAddress)
                        subscribe("")  // Subscribe to all topics
                    }
                    isConnected = true
                    startHeartbeat()
                    listenForMessages()
                    updateUI("Connected to server.")
                    return@launch  // Exit if connection is successful
                } catch (e: ZMQException) {
                    attempt++
                    updateUI("Connection attempt $attempt failed: ${e.message}")
                    if (attempt == maxRetries) {
                        updateUI("Max retries reached. Could not connect to server.")
                    } else {
                        delay(getBackoffDelay(attempt)) // Exponential backoff
                    }
                }
            }
        }
    }

    // Method to retry sending a message
    fun sendMessage(topic: String, message: String) {
        launch {
            var attempt = 0
            while (attempt < maxRetries) {
                try {
                    socket?.sendMore(topic)
                    socket?.send(message)
                    updateUI("Message sent: $message")
                    return@launch  // Exit if message is successfully sent
                } catch (e: ZMQException) {
                    attempt++
                    updateUI("Failed to send message. Attempt $attempt: ${e.message}")
                    if (attempt == maxRetries) {
                        updateUI("Max retries reached. Could not send message.")
                    } else {
                        delay(getBackoffDelay(attempt))  // Exponential backoff
                    }
                }
            }
        }
    }

    // Heartbeat sending method
    private fun startHeartbeat() {
        heartbeatJob = launch {
            while (isConnected) {
                try {
                    socket?.send("HEARTBEAT")
                    delay(5000)  // Send a heartbeat every 5 seconds
                } catch (e: ZMQException) {
                    updateUI("Heartbeat error: ${e.message}")
                    reconnect()  // Attempt to reconnect on heartbeat failure
                }
            }
        }
    }

    // Retry connection with exponential backoff
    private fun reconnect() {
        isConnected = false
        heartbeatJob?.cancel()
        socket?.close()
        updateUI("Reconnecting...")
        connect()
    }

    // Listen for incoming messages
    private fun listenForMessages() {
        launch {
            try {
                while (isConnected) {
                    val message = socket?.recvStr()
                    message?.let {
                        updateUI("Received message: $it")
                    }
                }
            } catch (e: ZMQException) {
                updateUI("Error receiving message: ${e.message}")
                reconnect()
            }
        }
    }

    // Exponential backoff delay calculation
    private fun getBackoffDelay(attempt: Int): Long {
        val backoff = retryDelayMillis * 2.0.pow(attempt.toDouble()).toLong()
        return min(backoff, maxRetryDelayMillis)  // Cap the delay at `maxRetryDelayMillis`
    }

    // Update the UI
    private fun updateUI(message: String) {
        launch(uiDispatcher) {
            // Example: LiveData or any other UI mechanism can be used here
            println(message)  // Replace with UI update logic
        }
    }

    // Disconnect and cleanup
    fun disconnect() {
        isConnected = false
        heartbeatJob?.cancel()
        socket?.close()
        updateUI("Disconnected from server.")
    }
}