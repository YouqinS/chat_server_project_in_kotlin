/*  Name: Youqin Sun
    Student ID: 1706219
    File Description:  this file maintains a collection of observers by adding/removing, and a collection of all messages by adding new messages to it,
                       and notifies observers when there is a new message
*/


object ChatHistory: MessageObservable{

    private val observers: MutableList<MessageOberver> = mutableListOf()
    private val myMessages = ArrayList<ChatMessage>()
    private val myGroupMessages :  MutableMap<String, ArrayList<ChatMessage>> = mutableMapOf()

    //register observer by adding it to the observers list
    @Synchronized override fun register(observer: MessageOberver) {
        observers.add(observer)
    }


    //unregister observer by removing it from the list
    @Synchronized override fun unregister(observer: MessageOberver) {
        observers.remove(observer)
    }

    //iterate through the observers list, and notify all when there is a new message
    @Synchronized override fun notifyObservers(chatMessage: ChatMessage) {
        for (observer in observers){
            //do not broadcast the message to observers which are "android-client_<timestamp>" because they only wants to know the results of their command.
            if(!observer.getUsername().startsWith("android-client_")) {
                observer.incomingMessage(chatMessage)
            }
        }
    }

//public message, for all, broadcasted to everybody
    @Synchronized fun addMessage(chatMessage: ChatMessage) {  //add  new message to messages list
        myMessages.add(chatMessage)
        notifyObservers(chatMessage) //call notifyObservers method to notify observers
    }


    @Synchronized fun addPrivateMessage(chatMessage: ChatMessage, toUser: String) {
        //not storing messages between 2 persons yet
        for (observer in observers){
            if(observer.getUsername().equals(chatMessage.userName) || observer.getUsername().equals(toUser)) {
                observer.incomingMessage(chatMessage)
            }
        }
    }

    @Synchronized fun getMessages(): List<ChatMessage> {  //when called, return a list of all history messages
        return myMessages
    }


    //in group chat, add new message to group message list and notify group members
    @Synchronized fun addMessage(chatMessage: ChatMessage,  group:String) {
        val messages =  myGroupMessages.getOrPut(group) {ArrayList<ChatMessage>()}
        messages.add(chatMessage)
        notifyObservers(chatMessage, group)
    }

    //iterate through observers and check if they are group members, if yes, notify about new message
    @Synchronized override fun notifyObservers(message: ChatMessage, group: String) {
        for (observer in observers){
            if(GroupManager.isMember(group,observer.getUsername())){
                observer.incomingGroupMessage(group, message)
            }
        }
    }

    //only used for unit testing
    @Synchronized fun clean() {
        myMessages.clear()
        myGroupMessages.clear()
        observers.clear()
    }

}