public class PasswordCrack {
    public static void main(String[] args) {
        if(args.length != 2){
            throw new Exception("Use: <dictionary> <passwd>");
        }

        List<String> dictionaryFile = Util.readFile(args[0]);
        List<String> passwordFile = Util.readFile(args[1]);
    }
}
