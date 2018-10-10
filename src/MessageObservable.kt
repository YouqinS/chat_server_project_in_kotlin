/*  Name: Youqin Sun
    Student ID: 1706219
    File Description: observerable interface, defines four methods to methods, to be implemented by inherited class
*/

interface MessageObservable {
    fun register(observer: MessageOberver)
    //methods to register and unregister observers

    fun unregister(observer: MessageOberver)


    //method to notify observers of change
    fun notifyObservers(chatMessage: ChatMessage)

    //method to notify observers of change in one specific chatgroup
    fun notifyObservers(message: ChatMessage, group: String)
}