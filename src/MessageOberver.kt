/*  Name: Youqin Sun
    Student ID: 1706219
    File Description: observer interface, defines three methods
    to be inherited by command interpreter class, methods to be implemented in command interpreter
*/


interface MessageOberver {
    fun incomingMessage(message: ChatMessage)
    fun getUsername():String
    fun incomingGroupMessage(group: String, message: ChatMessage)
    fun incomingPrivateMessage(userName:String, message: ChatMessage)

}