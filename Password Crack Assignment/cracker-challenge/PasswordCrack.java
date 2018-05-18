import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
/**
 * Created by Diaco Uthman 2018-05-18
 */
public class PasswordCrack {
    static char[] chars = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
    public static void main(String[] args) throws Exception{
      try{
          if(args.length != 2){
              throw new Exception("Use: <dictionary> <passwd>");
          }

          List<String> dictionaryFile = Utilities.readFile(args[0]);
          List<String> passwordFile = Utilities.readFile(args[1]);
          List<Crack> users = loadUserData(passwordFile,dictionaryFile);

          List<Thread> threads = new LinkedList<>();
          for(Crack user : users){
              threads.add(new Thread(user));
          }
          for(Thread t : threads){
              t.start();
          }
      }catch(Exception e){
        System.out.println(e.getMessage());
      }

    }

    public static List<Crack> loadUserData(List<String> lines,List<String> dict){
        List<Crack> users = new ArrayList<>();
        for(String line : lines){
            users.add(new Crack(new User(line),dict));
        }
        return users;
    }

    public static Set<String> mangle(String password){
        Set<String> mangle = new HashSet<>(150);

        mangle.add(password);

        // Prepending a character to the word
        for (int c = 'a'; c <= 'z'; c++)
          mangle.add((char)c + password);
        for (int c = 'A'; c <= 'Z'; c++)
          mangle.add((char)c + password);
        for (int c = '0'; c <= '9'; c++)
          mangle.add((char)c + password);

        // Appending a character to the word
        for (int c = 'a'; c <= 'z'; c++)
          mangle.add(password + (char)c);
        for (int c = 'A'; c <= 'Z'; c++)
          mangle.add(password + (char)c);
        for (int c = '0'; c <= '9'; c++)
          mangle.add(password + (char)c);

        // Delete first character
        mangle.add(password.substring(1));

        // Delete last character
        mangle.add(password.substring(0, password.length() - 1));

        // Reverse word
        mangle.add(new StringBuilder(password).reverse().toString());

        // Duplicate word
        mangle.add(password + password);

        // Reflecting the word both ways
        mangle.add(new StringBuilder(password).reverse() + password);
        mangle.add(password + new StringBuilder(password).reverse());

        mangle.add(password.toLowerCase());
        mangle.add(password.toUpperCase());

        // capitalize the string, e.g., String;
        mangle.add(password.substring(0, 1).toUpperCase() + password.substring(1));

        // ncapitalize the string, e.g., sTRING;
        mangle.add(password.substring(0, 1) + password.substring(1).toUpperCase());

        StringBuilder toggle1 = new StringBuilder();
        StringBuilder toggle2 = new StringBuilder();
        char[] upper = password.toUpperCase().toCharArray();
        char[] lower = password.toLowerCase().toCharArray();
        for (int i = 0; i < upper.length; i++) {
            if (i % 2 == 0) {
                toggle1.append(lower[i]);
                toggle2.append(upper[i]);
            } else {
                toggle1.append(upper[i]);
                toggle2.append(lower[i]);
            }
        }
        mangle.add(toggle1.toString());
        mangle.add(toggle2.toString());


        return mangle;
    }

    public static Set<String> mangle(Set<String> firstMangle){
        firstMangle.remove("");

        Set<String> secondMangle = new HashSet<>(10000);

        for (String word : firstMangle)
          secondMangle.addAll(mangle(word));

        secondMangle.remove("");

        return truncateMangle(secondMangle);
    }

    private static String truncate(String s) {
         if (s.length() > 8)
           return s.substring(0, 8);
         return s;
    }


    public static Set<String> truncateMangle(Set<String> mangle) {
        Set<String> newMangle = new HashSet<>(mangle.size());

        for (String s : mangle)
          newMangle.add(truncate(s));

        return newMangle;
    }

    private static class User{
        boolean cracked = false;
        String salt, hash, name[],wholeName, clearPassword="?";

        User(String line){
            String[] data = line.split(":");
            salt = data[1].substring(0,2);
            hash = data[1].substring(2);
            name = data[4].split("\\s+");
            wholeName=data[4];
        }

        User(){

        }

        public Set<String> seed(){
            Set<String> chosenList = mangle(this.name[0]);
            for(String namePart : this.name){
                if(!namePart.equals(this.name[0])){
                    chosenList.addAll(mangle(namePart));
                }
            }

            chosenList.addAll(mangle(mangle("1")));
            chosenList.addAll(mangle(mangle("12")));
            chosenList.addAll(mangle(mangle("123")));
            chosenList.addAll(mangle(mangle("1234")));
            chosenList.addAll(mangle(mangle("12345")));
            chosenList.addAll(mangle(mangle("123456")));
            chosenList.addAll(mangle(mangle("1234567")));
            chosenList.addAll(mangle(mangle("12345678")));
            return chosenList;
        }

        @Override
        public String toString(){
            String names="";
            for(String namePart: name){
              names += namePart;
            }
            String print = salt + " " + hash + " " + names + " " + clearPassword;
            if(cracked){
              return "YES " + print;
            }
            else
              return "NO " + print;
        }
    }

    private static class Crack implements Runnable{
        User user;
        List<String> dictionary;

        Crack(User user, List<String> currDict){
            this.user=user;
            this.dictionary=currDict;
        }

        public void testPassword(String password) throws InterruptedException{
            if(jcrypt.crypt(user.salt,password).equals(user.salt+user.hash)){
                System.out.printf("User: %s, Password: %s\n",user.wholeName,password);
                user.clearPassword=password;
                user.cracked=true;
                throw new InterruptedException();
            }
        }

        @Override
        public void run(){
            try{
                /* Stage 1 of cracking, check the names of the user */
                for(String word : user.seed()){
                    testPassword(word);
                }
                for(String word : this.dictionary){
                    for(String password : mangle(word)){
                        testPassword(password);
                    }
                }
                for(String word : this.dictionary){
                    for(String password : mangle(mangle(word))){
                        testPassword(password);
                    }
                }
            }catch(InterruptedException e){
                return;
            }
        }
    }
}
