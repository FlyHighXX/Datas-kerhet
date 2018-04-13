public class HillCipher{
  public static void main(String[] args)throws NumberFormatException, Exception{
      //Checking that the correct amount of arguments have been entered
      if(args.length != 3){
        throw new Exception("The input values were not entered correctly. Try again!");
      }
      // Checking radix
      int radix;
      try {
        radix = Integer.parseInt(args[0]);
        if(radix!=26){
          System.out.println("Only radix-value 26 is supported");
          return;
        }
      } catch(NumberFormatException e) {
        throw new NumberFormatException("Radix has to be an integer");
      }

      // Checking block size
      int block_size;
      try {
        block_size = Integer.parseInt(args[1]);
        if(block_size!=3){
          System.out.println("Only block-size value 3 is supported!");
          return;
        }
      } catch(NumberFormatException e) {
        throw new NumberFormatException("Block-size has to be an integer");
      }

      // Checking key-file
      String key_file_name = args[2];
      File txtfile = new File(key_file_name);

      System.out.println("radix: " + radix);
      System.out.println("block size:" + block_size);
      System.out.println("key-file name: " + key_file_name);
  }
}
