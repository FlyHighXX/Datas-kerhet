public class SimilarBits {
    public static void main(String[] args) {
        long h1  =0xc15c01d3365b51f2;
        long h12 =0xc5ec171f5fb91a1b;
        long h13 =0xb01c19ea33cf55ee;
        long h14 =0x618f36d869381588;
        long h2  =0x7ef8f221fa560414;
        long h22 =0x7f8daa5ace40306f;
        long h23 =0xc60ffb1cdb118bcf;
        long h24 =0xd98165bac9a4d028;
        String h1b = Integer.toBinaryString(h1)+Integer.toBinaryString(h12)+Integer.toBinaryString(h13)+Integer.toBinaryString(h14);
        String h2b = Integer.toBinaryString(h2)+Integer.toBinaryString(h22)+Integer.toBinaryString(h23)+Integer.toBinaryString(h24);
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
