public class SimilarBits {
    public static void main(String[] args) {
        int h1 = Integer.parseInt(args[0],16);
        int h2 = Integer.parseInt(args[1],16);
        String h1b = Integer.toBinaryString(h1);
        System.out.println(h1b);
        String h2b = Integer.toBinaryString(h2);
        System.out.println(h2b);
    }
}
