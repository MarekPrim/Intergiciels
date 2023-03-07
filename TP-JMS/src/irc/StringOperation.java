package irc;

public class StringOperation {
    
    public static boolean contains(String str, String src){
        //Check in str if src is present
        //If yes, return the string after src
        //If no, return null
        int index = str.indexOf(src);
        return index != -1;
    }
}
