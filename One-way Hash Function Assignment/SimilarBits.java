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
        //long h2  =0x7ef8f221fa560414;
        //long h22 =0x7f8daa5ace40306f;
        //long h23 =0xc60ffb1cdb118bcf;
        //long h24 =0xd98165bac9a4d028;
        String h1b = Integer.toBinaryString(h1)+Integer.toBinaryString(h12)+Integer.toBinaryString(h13)+Integer.toBinaryString(h14)+Integer.toBinaryString(h15)+Integer.toBinaryString(h16)+Integer.toBinaryString(h17)+Integer.toBinaryString(h18)+Integer.toBinaryString(h19)+Integer.toBinaryString(h110)+Integer.toBinaryString(h111)+Integer.toBinaryString(h112);
        //String h2b = Integer.toBinaryString(h2)+Integer.toBinaryString(h22)+Integer.toBinaryString(h23)+Integer.toBinaryString(h24);
        System.out.println(h1b.length());
    }
}
