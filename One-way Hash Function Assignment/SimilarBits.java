public class SimilarBits {
    public static void main(String[] args) {
        int h1 = Integer.parseInt(args[0],16);
        int h2 = Integer.parseInt(args[1],16);
        String h1b = Integer.toBinaryString(h1);
        String h2b = Integer.toBinaryString(h2);
        int length;
        if(h1b.length() > h2b.length()){
          length=h2b.length();
        }
        else{
          length=h1b.length();
        }
        int count=0;
        for(int i=0; i<length; i++){
          if(h1b.charAt(i)==h2b.charAt(i)){
            count++;
          }
        }
        System.out.println(count);
    }
}
