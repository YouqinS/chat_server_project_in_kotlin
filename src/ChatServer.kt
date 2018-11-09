/*  
    File Description: instantiate server socket, and wait for connection request,
    once connection is established, returns a socket for communication
    and instantiate command interpreter, and register it to chat history, and then start the command interpreter
*/


import java.net.ServerSocket
import java.net.Socket
import java.io.IOException



class ChatServer {
    private var serverSocket: ServerSocket? = null

    fun createLocalPortAndEstablishCommunication() {
        val serverSocket = ServerSocket(56536) //choose a port number(), or 0, then get any assigned port number
        println("ChatServer is listening on port " + serverSocket.localPort)
        while (true) {
            try {
                val client = serverSocket.accept() //wait for connection request, and returns a socket for communication
                startInterpreter(client)
            } catch (e: Exception) {
                println("Got exception: ${e.message}")

            } finally {
                println("all done")
            }
        }
    }


    private fun startInterpreter(socket: Socket) {
        println("new connection from " + socket.inetAddress.hostAddress + ":" + socket.port)
        val interpreter = CommandInterpreter(socket.getInputStream(), socket.getOutputStream())
        ChatHistory.register(interpreter)
        ConnectionManager.start(interpreter, socket)
    }
}
