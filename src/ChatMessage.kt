/*  
    File Description:  this file describes chat message: user input, user name, and time of user input
*/

import java.time.LocalDateTime


class ChatMessage (val message: String, val userName: String, val timestamp: LocalDateTime){

    override fun toString(): String{  //define message format
        return "$message from $userName at $timestamp"
    }

}
