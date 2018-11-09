
/*  
    File Description: starts and stops interpreter and stops socket
*/

import java.net.Socket

object ConnectionManager {
    private val sockets :MutableMap<CommandInterpreter, Socket> = mutableMapOf()
    private val threads :MutableMap<CommandInterpreter, Thread> = mutableMapOf()


    @Synchronized fun start(interpreter: CommandInterpreter, socket: Socket) {  //start command interpreter
        val thread = Thread(interpreter)
        threads.put(interpreter, thread)
        sockets.put(interpreter, socket)
        thread.start()
    }

    @Synchronized fun terminate(commandInterpreter: CommandInterpreter) {
        closeSocket(commandInterpreter)
        stopThread(commandInterpreter)
    }

    private fun stopThread(commandInterpreter: CommandInterpreter) { //stop command interpreter
        val thread = threads.get(commandInterpreter)
        if (thread != null) {
            thread.interrupt()
            threads.remove(commandInterpreter)
        }
    }

    private fun closeSocket(commandInterpreter: CommandInterpreter) {
        val socket = sockets.get(commandInterpreter)
        if (socket != null) {
            socket.close()
            if(socket.inetAddress != null) println("${socket.inetAddress.hostAddress} connection closed") // used for unit test
            sockets.remove(commandInterpreter)
        }
    }

    //for unit test
    @Synchronized fun isRunning(interpreter: CommandInterpreter)= threads.containsKey(interpreter)

    @Synchronized fun clean() = {
        sockets.clear()
        threads.clear()
    }
}
