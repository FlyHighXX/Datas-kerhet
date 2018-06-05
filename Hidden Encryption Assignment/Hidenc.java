import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import java.security.MessageDigest;

public class Hidenc{
    public static void main(String[] args) throws Exception{
        try{
            String[] mandatory = {"key","input","output"};
            String[] optional = {"ctr","offset","template","size"};
            String[] pickOne = {"template","size"};

            Map<String,String> options = OptionParser.parse(mandatory, optional, pickOne, args);
            byte[] key = hex2Byte(options.get("key"));
            byte[] input = readBinaryFile(options.get("input"));

            byte[] encryptedBlob;
            byte[] blob = EncodeHandler.createBlob(input,key);
            if(options.containsKey("ctr")){
                String ctrFile = options.get("ctr");
                encryptedBlob = EncodeHandler.encryptCTR(key,hex2Byte(ctrFile),blob);
            }else{
                encryptedBlob = EncodeHandler.encryptECB(key,blob);
            }
            byte[] template = null;
            int size = 1024;
            if (options.containsKey("template")) {
                template = readBinaryFile(options.get("template"));
                size = template.length;
            }
            else if (options.containsKey("size")){
                size = Integer.parseInt(options.get("size"));
            }
            int offset;
            if(options.containsKey("offset")){
                offset = Integer.parseInt(options.get("offset"));
            }else{
                Random rnd = new Random();
                offset = rnd.nextInt((size-encryptedBlob.length)/16)*16;
            }

            byte[] paddedEncryptedBlob;
            if(options.containsKey("template")){
                paddedEncryptedBlob = pad(encryptedBlob, offset, template);
            }else{
                paddedEncryptedBlob = pad(encryptedBlob, offset, size);
            }
            writeToFile(paddedEncryptedBlob, options.get("output"));
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }


    public static void writeToFile(byte[] output, String outputFileName) throws IOException{
        try {
          Files.write(Paths.get(outputFileName), output);
        } catch (IOException e) {
          throw new IOException(String.format("Couldn't write data to the file: \"%s\"", outputFileName));
        }
    }

    public static byte[] pad(byte[] data, int offset, int length){
        Random rnd = new Random();
        byte[] blob = new byte[length];
        rnd.nextBytes(blob);
        for(int i=0; i<data.length; i++){
            blob[i+offset] = data[i];
        }
        return blob;
    }

    public static byte[] pad(byte[] data, int offset, byte[] template){
        byte[] templateCpy = Arrays.copyOf(template, template.length);
        for (int i = 0; i < data.length; i++){
            templateCpy[i + offset] = data[i];
        }
        return templateCpy;
    }

    public static byte[] hex2Byte(String data) {
        BigInteger hex = new BigInteger(data, 16);
        byte[] hexByteArray = hex.toByteArray();

        if (hexByteArray.length > 16)
          hexByteArray = Arrays.copyOfRange(hexByteArray, hexByteArray.length - 16, hexByteArray.length);

        return hexByteArray;
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

    private static class OptionParser{
        public static Map<String,String> parse(String[] mandatory, String[] optional, String[] pickOne, String[] args) throws Exception{
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
            int count=0;
            for(String str : pickOne){
                if(resultSet.containsKey(str)){
                  count++;
                  if(count>1){
                      throw new Exception("Usage: Only one of --size=__ and --template=__");
                  }
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

    private static class EncodeHandler{
        public static byte[] createBlob(byte[] input, byte[] key)throws Exception{
            try{
                List<Byte> blob = new ArrayList<>(input.length + key.length*3);
                byte[] hash = MessageDigest.getInstance("MD5").digest(key);

                for(byte b: hash){
                    blob.add(b);
                }
                for(byte b: input){
                    blob.add(b);
                }
                for(byte b: hash){
                    blob.add(b);
                }
                for(byte b : MessageDigest.getInstance("MD5").digest(input)){
                    blob.add(b);
                }

                byte[] blobArray = new byte[blob.size()];
                for(int i=0; i<blob.size(); i++){
                    blobArray[i] = blob.get(i);
                }

                return blobArray;
            }catch(Exception e){
              throw new Exception(e.getMessage());
            }
        }

    public static byte[] encryptCTR(byte[] key, byte[] ctr, byte[] input) throws Exception{
        try{
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            IvParameterSpec vSpec = new IvParameterSpec(ctr);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE,keySpec,vSpec);
            return cipher.doFinal(input);
        }catch(BadPaddingException e){
            throw new BadPaddingException(e.getMessage());
        }
    }

    public static byte[] encryptECB(byte[] key, byte[] input) throws Exception{
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE,keySpec);
            return cipher.doFinal(input);
        }catch(BadPaddingException e){
            throw new BadPaddingException(e.getMessage());
        }
    }
  }
}
