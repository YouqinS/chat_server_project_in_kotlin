/*  
    File Description: this file contains a users collection set, a method to add user to its collection, and a method to remove
*/


object Users{

    private val  users: MutableSet<String> = mutableSetOf()

    //add user to user collection
    @Synchronized fun addUser(userName:String){
        users.add(userName)
    }

    @Synchronized fun exists(userName:String) :Boolean {
        return users.contains(userName);
    }

    //clean data when user leaves, so it can come back with same user name
    @Synchronized fun remove(userName: String) {
        users.remove(userName)
    }

    //for unit test
    @Synchronized fun clean() = users.clear()

    @Synchronized fun getListOfUsersSeparatedByComa(): String {
        return users.joinToString(prefix = "", postfix = "", separator = "\r\n")
    }

}
