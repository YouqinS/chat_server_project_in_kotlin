/*  
    File Description:  this file handles group creating, adding group members,  and collecting group messages and notifying group members
*/

import java.time.LocalDateTime

object GroupManager {
     val groups:MutableSet<String> = mutableSetOf()
    private val owners:MutableMap<String,String> = mutableMapOf() //group to owner
    private val members:MutableMap<String,MutableSet<String>> = mutableMapOf() //group members (including owner)

    @Synchronized fun createGroup(group: String, owner: String) {
        if (!groups.contains(group)) {  //check whether group exits already, if not, add to collection
            groups.add(group)
            owners.put(group, owner) //store group name and owner name in pair
            val usernames = mutableSetOf(owner)
            members.put(group, usernames) //store group member names
            val chatMessage = ChatMessage("new group '$group' available", owner, LocalDateTime.now())
            ChatHistory.notifyObservers(chatMessage) //do not store message in history
        }
    }

    @Synchronized fun exists(group: String)= groups.contains(group)


    @Synchronized fun isMember(group:String, username: String) :Boolean{
        return members.getOrElse(group) { mutableSetOf() }.contains(username)
    }

    @Synchronized fun addMember( username: String, group:String) {
        if(!Users.exists(username)) { //do not add unknown user
            return
        }

        val isAdded = members?.get(group)?.add(username) //only add user if group exists
        if (isAdded == true) {//https://stackoverflow.com/questions/32830904/use-of-boolean-in-if-expression
            val chatMessage = ChatMessage("$username registered as group member of '$group'", "<server>", LocalDateTime.now())
            ChatHistory.notifyObservers(chatMessage) //do not store message in history
        }
    }

    //for unit test
    @Synchronized fun isOwner(group:String, username: String) = owners[group].equals(username)
    @Synchronized fun clean() {
        groups.clear()
        owners.clear()
        members.clear()
    }

}
