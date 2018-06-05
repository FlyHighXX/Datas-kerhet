import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import java.util.Base64;

/*
*
*   AUTHOR: Diaco Uthman
*   DATE: 2018-05-26
*/

public class Hiddec {
    public static void main(String[] args) throws Exception{
        try{
            String[] mandatory = {"key","input","output"};
            String[] optional = {"ctr"};
            Map<String,String> options = OptionParser.parse(mandatory, optional, args);

            String keyFile = options.get("key");
            byte[] inputFile = readBinaryFile(options.get("input"));

            EncodeFinder encFinder;
            if(options.get("ctr") != null){
                  String ctrFile = options.get("ctr");
                encFinder = new EncodeFinder(hex2Byte(keyFile),hex2Byte(ctrFile),inputFile);
                writeToFile(encFinder.findDataCTR(), options.get("output"));
            }else{
                encFinder = new EncodeFinder(hex2Byte(keyFile), inputFile);
                writeToFile(encFinder.findDataECB(), options.get("output"));
            }
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    static byte[] hex2Byte(String data) {
        BigInteger hex = new BigInteger(data, 16);
        byte[] hexByteArray = hex.toByteArray();

        if (hexByteArray.length > 16)
          hexByteArray = Arrays.copyOfRange(hexByteArray, hexByteArray.length - 16, hexByteArray.length);

        return hexByteArray;
    }

    public static List<String> readFile(String fileName) throws IOException{
        Path file = Paths.get(fileName);
        List<String> lines;
        try {
          lines = Files.readAllLines(file, Charset.defaultCharset());
        } catch (IOException e) {
          throw new IOException(String.format("Couldn't read the file: \"%s\"\n", fileName));
        }
        List<String> emptyLines = new ArrayList<>();
        for (String line : lines){
            if (line.equals("")){
                emptyLines.add(line);
            }
        }
        lines.removeAll(emptyLines);
        return lines;
    }
    public static byte[] readBinaryFile(String fileName) throws IOException{
        Path file = Paths.get(fileName);
        byte[] lines;
        try {
          lines = Files.readAllBytes(file);
        } catch (IOException e) {
          throw new IOException(String.format("Couldn't read the file: \"%s\"\n", fileName));
        }
        return lines;
    }

    public static void writeToFile(byte[] output, String outputFileName) throws IOException{
        try {
          Files.write(Paths.get(outputFileName), output);
        } catch (IOException e) {
          throw new IOException(String.format("Couldn't write data to the file: \"%s\"", outputFileName));
        }
    }

    private static class OptionParser{
        public static Map<String,String> parse(String[] mandatory, String[] optional, String[] args) throws Exception{
            Set<String> options = new HashSet<>();
            options.addAll(Arrays.asList(mandatory));
            options.addAll(Arrays.asList(optional));

            Map<String, String> resultSet = new HashMap<>();
            for(String str : args){
                String[] splitString = str.split("=");
                String optionName = splitString[0].substring(2);
                String optionValue = splitString[1];
                if(options.contains(optionName)){
                    resultSet.put(optionName, optionValue);
                }
            }
            for(String option : mandatory){
                if(!resultSet.containsKey(option)){
                    throw new Exception("Usage: --key=__ [--ctr=__ --size=__ --template=__ offset=__] --input=__ --output=__ ");
                }
            }
            return resultSet;
        }
    }

    private static class EncodeFinder{
        byte[] key,ctr,input,hash;
        EncodeFinder(byte[] key, byte[] ctr, byte[] input)throws NoSuchAlgorithmException{
            this.key=key;
            this.ctr=ctr;
            this.input=input;
            try{
                this.hash=MessageDigest.getInstance("MD5").digest(key);
            }catch(NoSuchAlgorithmException e){
                throw new NoSuchAlgorithmException("Could not create MD5 hash");
            }

        }

        EncodeFinder(byte[] key, byte[] input) throws NoSuchAlgorithmException{
            this.key=key;
            this.input=input;
            try{
                this.hash=MessageDigest.getInstance("MD5").digest(key);
            }catch(NoSuchAlgorithmException e){
                throw new NoSuchAlgorithmException("Could not create MD5 hash");
            }
        }

        public byte[] findDataCTR() throws Exception{
            byte[] encData = {};
            for(int i=0; i<input.length; i+=16){
                encData = AESEncryptor.decryptCTR(key, ctr, Arrays.copyOfRange(input,i,input.length));
                if(testBlob(encData)){
                    break;
                }
            }
            if(!testBlob(encData)){
                throw new Exception("Could not find blob");
            }
            for(int i=hash.length; i<encData.length; i++){
                if(testBlob(encData,i)){
                    byte[] foundData = Arrays.copyOfRange(encData,hash.length,i);
                    int start = i;
                    start += hash.length;
                    byte[] validationData = Arrays.copyOfRange(encData,start,start+hash.length);
                    if(validate(MessageDigest.getInstance("MD5").digest(foundData),validationData)){
                        return foundData;
                    }
                    else{
                        throw new Exception("Data could not be validated");
                    }
                }
            }
            throw new Exception("Data could not be found in the blob");
        }

        public byte[] findDataECB() throws Exception{
            byte[] encData = {};
            for(int i=0; i<input.length; i+=16){
                encData = AESEncryptor.decryptECB(key, Arrays.copyOfRange(input,i,input.length));
                if(testBlob(encData)){
                    break;
                }
            }
            if(!testBlob(encData)){
                throw new Exception("Could not find blob");
            }
            for(int i=hash.length; i<encData.length; i++){
                if(testBlob(encData,i)){
                    byte[] foundData = Arrays.copyOfRange(encData,hash.length,i);
                    int start = i;
                    start += hash.length;
                    byte[] validationData = Arrays.copyOfRange(encData,start,start+hash.length);
                    if(validate(MessageDigest.getInstance("MD5").digest(foundData),validationData)){
                        return foundData;
                    }
                    else{
                        throw new Exception("Data could not be validated");
                    }
                }
            }
            throw new Exception("Data could not be found in the blob");
        }

        public boolean testBlob(byte[] data){
            return Arrays.equals(hash, Arrays.copyOfRange(data,0,hash.length));
        }

        public boolean testBlob(byte[] data, int offset){
            return Arrays.equals(hash,Arrays.copyOfRange(data,offset,offset+hash.length));
        }

        public boolean validate(byte[] data, byte[] validationData){
            return Arrays.equals(data,validationData);
        }

    }

    private static class AESEncryptor{
        public static byte[] decryptCTR(byte[] key, byte[] ctr, byte[] encrypted) throws Exception{
            try{
                Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
                IvParameterSpec ivSpec = new IvParameterSpec(ctr);
                SecretKeySpec sKey = new SecretKeySpec(key, "AES");
                cipher.init(Cipher.DECRYPT_MODE, sKey, ivSpec);
                return cipher.doFinal(encrypted);

            }catch(BadPaddingException e){
                throw new BadPaddingException(e.getMessage());
            }
        }

        public static byte[] decryptECB(byte[] key, byte[] encrypted) throws Exception{
            try{
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                SecretKeySpec sKey = new SecretKeySpec(key, "AES");
                cipher.init(Cipher.DECRYPT_MODE, sKey);
                return cipher.doFinal(encrypted);

            }catch(BadPaddingException e){
                throw new BadPaddingException(e.getMessage());
            }
        }
    }
}
