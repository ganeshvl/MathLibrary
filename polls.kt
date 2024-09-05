import org.zeromq.ZMQ
import org.zeromq.ZMQ.Poller
import org.zeromq.ZMQ.Socket
import org.zeromq.ZContext

fun main() {
    // Create a context
    val context = ZContext()

    // Create a socket to connect to a server
    val socket: Socket = context.createSocket(ZMQ.SUB)
    
    // Set up your server address and port
    val address = "tcp://localhost:5555"

    // Connect the socket
    socket.connect(address)
    println("Connecting to $address")

    // Subscribe to all messages (empty string subscribes to all)
    socket.subscribe("".toByteArray())

    // Set up poller to handle events
    val poller = Poller(1)
    poller.register(socket, Poller.POLLIN)

    // Event loop to handle connection/disconnection events
    while (!Thread.currentThread().isInterrupted) {
        val rc = poller.poll(1000)  // Poll with a timeout

        if (rc == -1) break

        // Handle events
        if (poller.pollin(0)) {
            val message = socket.recvStr()
            println("Received message: $message")

            // Process your message here...
        }

        // Simulate ZMQ_EVENT_CONNECTED and ZMQ_EVENT_DISCONNECTED
        if (/* Check connection status */) {
            println("ZMQ_EVENT_CONNECTED")
        } else {
            println("ZMQ_EVENT_DISCONNECTED")
        }
    }

    // Clean up
    socket.close()
    context.close()
}



#############

import org.zeromq.ZMQ
import org.zeromq.ZMQ.Event
import org.zeromq.ZMQ.Socket
import org.zeromq.ZContext

fun main() {
    val context = ZContext()

    // Create socket and monitor
    val socket: Socket = context.createSocket(ZMQ.REQ)
    val monitorSocket: Socket = context.createSocket(ZMQ.PAIR)

    // Setup monitor to track events
    socket.monitor("inproc://monitor", ZMQ.EVENT_CONNECTED or ZMQ.EVENT_DISCONNECTED)
    monitorSocket.connect("inproc://monitor")

    // Connect to some server
    socket.connect("tcp://localhost:5555")

    while (true) {
        val event = Event.recv(monitorSocket)
        when (event.event) {
            ZMQ.EVENT_CONNECTED -> println("ZMQ_EVENT_CONNECTED: ${event.address}")
            ZMQ.EVENT_DISCONNECTED -> println("ZMQ_EVENT_DISCONNECTED: ${event.address}")
        }
    }

    // Clean up
    socket.close()
    monitorSocket.close()
    context.close()
}