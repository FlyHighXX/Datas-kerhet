public class HillCipher{
  public static void main(String[] args)throws NumberFormatException{
      int radix;
      try {
        radix = Integer.parseInt(args[0]);
        if(radix!=26){
          throw new Exception("Only radix-value 26 is supported!");
        }
      } catch(NumberFormatException e) {
        throw new NumberFormatException("Radix has to be an integer");
      }

      int block_size;
      try {
        block_size = Integer.parseInt(args[1]);
        if(block_size!=3){
          System.err.println("Only block-size value 3 is supported!");
        }
      } catch(NumberFormatException e) {
        throw new NumberFormatException("Block-size has to be an integer");
      }

      System.out.println("radix: " + radix);
      System.out.println("block size:" + block_size);
  }

}
