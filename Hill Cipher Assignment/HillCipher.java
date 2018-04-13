import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.ModuloInteger;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Matrix;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class HillCipher{
  private int radix,block_size;
  private void setRadix(String radix) throws NumberFormatException,Exception{
      try {
        this.radix = Integer.parseInt(radix);
        if(this.radix!=26){
          throw new Exception("Only radix-value 26 is supported");
        }
      } catch(NumberFormatException e) {
        throw new NumberFormatException("Radix has to be an integer");
      }
  }

  private void setBlockSize(String block_size) throws NumberFormatException,Exception{
      try {
        this.block_size = Integer.parseInt(block_size);
        if(this.block_size!=3){
          throw new Exception("Only block-size value 3 is supported!");
        }
      } catch(NumberFormatException e) {
        throw new NumberFormatException("Block-size has to be an integer");
      }
  }

  private Matrix<ModuloInteger> createKeyMatrix(String key_file_name) throws FileNotFoundException, Exception{
      Scanner scanner;
      File file = new File(key_file_name);
      try{
          scanner = new Scanner(file);
      } catch(FileNotFoundException e){
          throw new FileNotFoundException("This file /" + key_file_name + " was not found.");
      }

      ModuloInteger[][] tempMatrix = new ModuloInteger[this.block_size][this.block_size];
      try{
          for(int i=0; i<3; i++){
              String[] splitLine = scanner.nextLine().split(" ");
              if(splitLine.length != 3){
                throw new Exception("The entered key was not a 3x3 matrix");
              }
              for(int j=0; j<3; j++){
                  tempMatrix[i][j]=ModuloInteger.valueOf(LargeInteger.valueOf(splitLine[j]));
              }
          }
          if(scanner.hasNextLine()){
            throw new Exception("The entered key was not a 3x3 matrix");
          }
      }catch(IndexOutOfBoundsException e){
          throw new Exception("The entered file was not a NxN matrix of block-size 3");
      }
      // We already know that this matrix is 3x3. Therefore, no checks need to be made.
      return DenseMatrix.valueOf(tempMatrix);
  }

  public byte[] readMsgFromAFile(String file_name) throws FileNotFoundException, IOException{
      byte[] data;
      try{
          String path = "./" + file_name;
          Path p = Paths.get(path);
          data = Files.readAllBytes(p);
      }catch(FileNotFoundException e){
          throw new FileNotFoundException("This file /" + file_name + " was not found.");
      }catch (IOException e) {
          throw new IOException("Something went wrong.");
      }
      return data;
  }

  public void encryptMsg(byte[] msg, Matrix<ModuloInteger> key){

  }

  private void printInformation(){
      System.out.println("radix: " + this.radix);
      System.out.println("block size:" + this.block_size);

  }

  private HillCipher(String[] args) throws NumberFormatException,Exception{
      //Checking that the correct amount of arguments have been entered
      if(args.length != 3){
        throw new Exception("The input values were not entered correctly. Enter on the form <radix> <block size> <key-file> <plaintext-file> <cipherfile>");
      }
      setRadix(args[0]);
      setBlockSize(args[1]);
  }

  public static void main(String[] args)throws NumberFormatException, Exception{
      HillCipher currCipher = new HillCipher(args);

      // Generating the matrix from the key-file
      Matrix<ModuloInteger> key_matrix = currCipher.createKeyMatrix(args[2]);
      byte[] msg = currCipher.readMsgFromAFile(args[3]);

      System.out.println(msg);

      System.out.println(key_matrix.get(0,0));
      currCipher.printInformation();
  }
}
