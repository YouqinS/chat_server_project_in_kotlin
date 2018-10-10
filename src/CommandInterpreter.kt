/*  Name: Youqin Sun
    Student ID: 1706219
    File Description: this file shows instructions at the beginning and handles user input based on input content
*/


import java.io.*
import java.time.LocalDateTime

class CommandInterpreter(input: InputStream, output: OutputStream, var userName: String = "") : Runnable, MessageOberver {


    private val out = PrintWriter(output, true); // true means println will flush the output buffer
    private val inn = BufferedReader(InputStreamReader(input))
    private var isConnectionClosedByUser = false

    override fun run() {
        showInstructions() //call method to show instructions

        //while connected, keep reading user input and calling interpret method to interpret the input
        while (!isConnectionClosedByUser) {
            val text = inn.readLine()
            when(text) {
                null -> checkIfClientHasClosedItsConnection() // client closed its telnet windows before sending :exit command
                "" -> doNothing() //client pressed enter multiple times, ignore text, do nothing
                else  -> interpret(text)
            }
        }
    }

    private fun doNothing() {
    }


    private fun interpret(userInput: String) { //interpret user input and call different methods based on input type
        when {
            isUsernameMissingAndNotGoingToBeSet(userInput) -> out.println("User name not set. Use command :user to set it")
            isMessageForGroupOrUser(userInput) -> sendMessage(userInput)
            isCommand(userInput) -> processCommand(userInput)
            else -> addMessageToChatHistory(userInput)
        }
    }


    private fun addMessageToChatHistory(userInput: String) {
        val chatMessage = ChatMessage(userInput, userName, LocalDateTime.now())
        ChatHistory.addMessage(chatMessage)
    }


    private fun sendMessage(userInput: String) {
        val tokens = userInput.trim().split(" ")
        val toGroupOrUser = tokens[0].substring(1)
        val text = userInput.removePrefix(tokens[0]).trim()

        if (text.isEmpty()) {
            return
        }

        when{
            text.isEmpty() -> doNothing()
            Users.exists(toGroupOrUser) -> addMessageToPrivateHistory(userInput, userName, toGroupOrUser)
            GroupManager.exists(toGroupOrUser) ->  addMessageToGroupChatHistory(text,toGroupOrUser)
            else -> out.println("Error :unknown '$toGroupOrUser'")
        }


    }

    private fun addMessageToPrivateHistory(text: String, fromUser: String, toUser: String) {
        val chatMessage = ChatMessage(text, userName, LocalDateTime.now())
        ChatHistory.addPrivateMessage(chatMessage, toUser)
    }


    private fun addMessageToGroupChatHistory(text: String, group:String) {
        val chatMessage = ChatMessage(text, userName, LocalDateTime.now())
        ChatHistory.addMessage(chatMessage, group)
    }



    private fun processCommand(userInput: String) { //when user input is a command, process command
        when {
            isThisTheCommandForSettingUsernameWithCorrectSyntax(userInput) -> setUsername(userInput)
            userInput.startsWith(":create ") -> createGroup(userInput)
            userInput.startsWith(":addmember ") -> addMember(userInput)
            else -> when (userInput) {
                ":messages" -> out.println(ChatHistory.getMessages().joinToString(prefix = "", postfix = "", separator = "\r\n"))
                ":groups" -> out.println(GroupManager.groups.joinToString("\n", "", ""))
                ":users" -> out.println(Users.getListOfUsersSeparatedByComa())
                ":exit" -> shutdown()
                else -> out.println("Did not get it :unknow command")
            }
        }
    }






    private fun isMessageForGroupOrUser(userInput: String) = userInput[0] == '@' //check whether input is a group message

    private fun isCommand(userInput: String) = userInput[0] == ':' //check whether input is a command

    //check whether user name has been set, if not, is this the command to set it?
    private fun isUsernameMissingAndNotGoingToBeSet(userInput: String): Boolean {
        return userName.isEmpty() && !isThisTheCommandForSettingUsernameWithCorrectSyntax(userInput.trim())
    }


    private fun addMember(userInput: String) { //add group member
        val tokens = userInput.trim().split(" ")
        if (tokens.size != 3) { //check whether command is complete and correct
            out.println("Group name or username not set: addmember <groupname> <username>")
        } else {
            val groupName = tokens[1]
            val username = tokens[2]
            GroupManager.addMember( username, groupName)
        }

    }

    private fun createGroup(userInput: String) { //create group for group chat
        val groupname = userInput.subSequence(":group".length + 1, userInput.lastIndex + 1).trim().toString()

        when {
            groupname.isEmpty() -> out.println("Group name not set: no group name specified")
            GroupManager.exists(groupname) -> out.println("group exits already, set another one")
            Users.exists(groupname) -> out.println("'$groupname' is the name of an existing user, set another one")
            else -> {
                GroupManager.createGroup(groupname, userName)
                out.println("Group '$groupname' created")
            }
        }

//        if (groupname.isEmpty()) {
//            out.println("Group name not set: no group name specified")
//        } else {
//            if (GroupManager.exists(groupname)) {
//                out.println("group exits already, set another one")
//            } else {
//                GroupManager.createGroup(groupname, userName)
//                out.println("Group '$groupname' created")
//            }
//        }

    }

    private fun setUsername(userInput: String) { //set user name
        val name = userInput.subSequence(6, userInput.lastIndex + 1).trim().toString()
        when {
            name.isEmpty() -> out.println("User name not set: no user name specified")
            GroupManager.exists(name) -> out.println("'$name' is reserved for a group, set another one")
            else -> {
                userName = name
                out.println("User name set to $name")
                Users.addUser(name)
            }
        }
//        if (name.trim().isEmpty()) { //check whether input is empty
//            out.println("User name not set: no user name specified")
//        } else {
//            userName = name.toString()
////            if (Users.exists(userName)) { //check whether user name has been used
////                out.println("user name exits already, set another one")
////            } else {
//                out.println("User name set to $name")
//                Users.addUser(name.toString())
//
//        }
    }

    //do not accept command ":users" or ":users "
    private fun isThisTheCommandForSettingUsernameWithCorrectSyntax(userInput: String) = userInput.startsWith(":user ")



    private fun showInstructions() {//print instructions when connected
        val message =
                "To set user name, use command :user username\r\n" +
                        "To see users, use command :users\r\n" +
                        "To see history messages, use command :messages\r\n" +
                        "To see all groups, use command :groups\r\n" +
                        "To exit, use command :exit\r\n" +
                        "To create a group, use command :create groupname\r\n" +
                        "To add a member to a group, use command :addmember groupname username\r\n" +
                        "To send a message to a group, use command @<groupname>\r\n" +
                        "To send a message to a user, use command @<username>\r\n"
        out.println(message)
    }

    private fun shutdown() { //user quit and disconnect
        out.println("goodbye $userName, it was fun")
        stopCommunicationAndRemoveUser()
    }

    private fun stopCommunicationAndRemoveUser() {
        isConnectionClosedByUser = true
        ConnectionManager.terminate(this)
        Users.remove(userName)
    }

    private fun checkIfClientHasClosedItsConnection() {
        println("lost client connection  " + if(userName != null) "($userName)" else "" )
        stopCommunicationAndRemoveUser()
    }


    //show new message to observers
    override fun incomingMessage(message: ChatMessage) {
        out.println("${message.message} from ${message.userName}")
    }

    override fun getUsername() = userName

    override fun incomingGroupMessage(group: String, message: ChatMessage) {
        out.println("from @$group: " + "${message.message} from ${message.userName}")
    }

    override fun incomingPrivateMessage(userName: String, message: ChatMessage) {
  //      out.println("@$userName" + "${message.message} from ${message.userName}")
        out.println("${message.message} from ${message.userName}")
    }



}
