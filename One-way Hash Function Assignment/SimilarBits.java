public class SimilarBits {
    public static void main(String[] args) {
        int h1  =0xc15c01d;
        int h12 =0x3365b51;
        int h13 =0xf2;
        int h14 =0xc5ec171;
        int h15 =0xf5fb91a;
        int h16 =0x1b;
        int h17 =0xb01c19e;
        int h18 =0xa33cf55;
        int h19 =0xee;
        int h110=0x618f36d;
        int h111=0x8693815;
        int h112=0x88;

        int h2  =0x7ef8f22;
        int h21 =0x1fa5604
        int h22 =0x14;
        int h23 =0x7f8daa5;
        int h24 =0xace4030
        int h25 =0x6f;
        int h26 =0xc60ffb1;
        int h27 =0xcdb118b;
        int h28 =0xcf;
        int h29 =0xd98165b;
        int h210=0xac9a4d0
        int h211=0x28;

        String h1b = Integer.toBinaryString(h1)+Integer.toBinaryString(h12)+Integer.toBinaryString(h13)+Integer.toBinaryString(h14)+Integer.toBinaryString(h15)+Integer.toBinaryString(h16)+Integer.toBinaryString(h17)+Integer.toBinaryString(h18)+Integer.toBinaryString(h19)+Integer.toBinaryString(h110)+Integer.toBinaryString(h111)+Integer.toBinaryString(h112);
        String h2b = Integer.toBinaryString(h2)+Integer.toBinaryString(h22)+Integer.toBinaryString(h23)+Integer.toBinaryString(h24)+Integer.toBinaryString(h25)+Integer.toBinaryString(h26)+Integer.toBinaryString(h27)+Integer.toBinaryString(h28)+Integer.toBinaryString(h29)+Integer.toBinaryString(h210)+Integer.toBinaryString(h211);
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
