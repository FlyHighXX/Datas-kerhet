public class SimilarBits {
    public static void main(String[] args) {
        int h1 = 0x5ffef991b40ca0935151157c13ef6bf7;
        int h2 = 0x712471ea8829b0fea7621ca49ae2bc97;
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
