import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.ModuloInteger;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Matrix;
import org.jscience.mathematics.vector.Vector;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.util.ArrayList;

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

  public Vector<ModuloInteger> readMsgFromAFile(String file_name) throws FileNotFoundException, IOException{
      Scanner scanner;
      File file = new File(file_name);
      try{
        scanner = new Scanner(file);
      }catch (FileNotFoundException e) {
          throw new FileNotFoundException("<plaintext>-file was not found");
      }
      ArrayList<Integer> array = new ArrayList<Integer>();
      while(scanner.hasNextLine()){
          String[] splitLine = scanner.nextLine().split(" ");
          for(int j=0; j<splitLine.length; j++){
              array.add(Integer.parseInt(splitLine[j]));
          }
      }
      array = truncateArrayList(array);
      ModuloInteger[] data = new ModuloInteger[array.size()];
      for(int i=0; i<array.size(); i++){
        data[i]=ModuloInteger.valueOf(LargeInteger.valueOf(array.get(i)));
      }
      return DenseVector.valueOf(data);
  }

  private ArrayList<Integer> truncateArrayList(ArrayList<Integer> array){
      if(array.size()%3 == 1){
        array.remove(array.size()-1);
      }
      else{
        array.remove(array.size()-1);
        array.remove(array.size()-2);
      }
      return array;
  }

  public void encryptMsg(Vector<ModuloInteger> msg, Matrix<ModuloInteger> key){

  }

  private void printInformation(){
      System.out.println("radix: " + this.radix);
      System.out.println("block size:" + this.block_size);

  }

  private HillCipher(String[] args) throws NumberFormatException,Exception{
      //Checking that the correct amount of arguments have been entered
      if(args.length != 4){
        throw new Exception("The input values were not entered correctly. Enter on the form <radix> <block size> <key-file> <plaintext-file> <cipherfile>");
      }
      setRadix(args[0]);
      setBlockSize(args[1]);
  }

  public static void main(String[] args)throws NumberFormatException, Exception{
      HillCipher currCipher = new HillCipher(args);

      // Generating the matrix from the key-file
      Matrix<ModuloInteger> key_matrix = currCipher.createKeyMatrix(args[2]);
      Vector<ModuloInteger> msg = currCipher.readMsgFromAFile(args[3]);
      Vector<ModuloInteger> encrypted = currCipher.encryptMsg(msg,key_matrix);

      System.out.println(msg.get(0));
      System.out.println(key_matrix.get(0,0));
      currCipher.printInformation();
  }
}
