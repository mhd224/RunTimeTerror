package code;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.print.event.PrintJobListener;

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
        int fileCounter =0;
        String firstFile = "";
        Boolean printLedger = false;
        System.out.println("Enter test file name (i.e code/input[1-5].txt)");
        while ( (fileName = input.nextLine()).isEmpty() == false) {
            try{ 
                rootNodes.add(treeMaker.createMerkleTree(fileName));
                if (fileCounter ==0) {
                    firstFile = fileName;
                }
                fileCounter++;
            }
            catch( FileNotFoundException e){
                System.out.println("File not found " + fileName);
            }
            System.out.println("Enter new test file name OR press ENTER to finish");
        }
        System.out.println("Number of files being read: " + fileCounter);
        while (true) {
            System.out.println("Would you like to print the ledger? (y/n): ");
            String str = input.next();
            if (str.equalsIgnoreCase("Y")) {
                printLedger = true;
                break;
            } else if (str.equalsIgnoreCase("N")) {
                break;
            }
        }

        ArrayList<Block> blockArray = new ArrayList<>();

        for (int i = 0; i < fileCounter; i++) {
            String hashOfPrevBlockHeader;
            if(i == 0) {
                hashOfPrevBlockHeader = "0";
            } else {
                hashOfPrevBlockHeader = blockArray.get(i-1).getHeaderHash();
            }
            int time = (int) (new Date().getTime()/1000);
            Block newBlock = new Block(hashOfPrevBlockHeader, rootNodes.get(i).getContent().getHash(), time, rootNodes.get(i));
            blockArray.add(newBlock);
        }
        printBlocks(blockArray, firstFile, printLedger, fileCounter);

    }
    public static void printBlocks(ArrayList<Block> blockArray, String filename, Boolean printLedger, int fileCounter) throws Exception {
        //block objects already made
        String trimmedFileName = filename.substring(0, filename.lastIndexOf('.'));
        trimmedFileName = trimmedFileName +".block.out";

        //print block objects to FIRST FileName.block.out
        FileWriter myWriter = new FileWriter(trimmedFileName);
        for (int i = fileCounter-1; i>= 0; i--) {
            myWriter.write("BEGIN BLOCK\n");
            myWriter.write("BEGIN HEADER\n");
            myWriter.write(blockArray.get(i).getHashPrevBlock() + "\n");
            myWriter.write(blockArray.get(i).getHashOfRoot() + "\n");
            myWriter.write(blockArray.get(i).getTime() + "\n");
            myWriter.write(blockArray.get(i).getBoundary() + "\n");
            myWriter.write(blockArray.get(i).getNonce() + "\n");
            myWriter.write("END HEADER\n");
            if(printLedger) {
                myWriter.write(blockArray.get(i).getLedgerRoot().printMerkleTree(blockArray.get(i).getLedgerRoot()));
            }
            myWriter.write("END BLOCK\n\n");

        }
        myWriter.close();
    }
}
