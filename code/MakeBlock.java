package code;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.Scanner;

public class MakeBlock {
    public static void main(String[] args) throws Exception{
        MakeTree treeMaker = new MakeTree();
        Scanner input = new Scanner(System.in);        
        String fileName;
        ArrayList<Node> rootNodes = new ArrayList<>();
        System.out.println("Enter test file name (i.e code/input.txt)");
        while ( (fileName = input.nextLine()).isEmpty() == false) {
            try{ 
                rootNodes.add(treeMaker.createMerkleTree(fileName));
            }
            catch( FileNotFoundException e){
                System.out.println("File not found " + fileName);
            }
            System.out.println("Enter new test file name OR press ENTER to finish");
        }

        //make block objects

        //print block objects to FIRST FileName.block.out

        


    }
}
