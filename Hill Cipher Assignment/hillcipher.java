public class HillCipher{
  public static void main(String[] args) {
      int radix=0;
      int block_size=0;
      try {
        radix = Integer.parseInt(args[0]);
        block_size = Integer.parseInt(args[1]);
      } catch(NumberFormatException e) {

      }

      System.out.println("radix: " + radix);
      System.out.println("block size:" + block_size);
  }

}
