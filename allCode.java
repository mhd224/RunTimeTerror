//all our source code in one file (this file will not compile, only here for moss and code review from grader)

//hw3

//Content.java
package code;
/**
 * Use for non-lead nodes
 * Parent Class for LeafContent
 * 
 */
public class Content {
    private String hash;
    private String address;
    private String balance;

    public Content(String hash, String address, String balance){
        this.hash = hash;
        this.address = address;
        this.balance = balance;
    }
    public String getHash(){
        return hash;
    }
    public String getAddress(){
        return address;
    }
    public String getBalance(){
        return balance;
    }
}

//MakeTree.java
package code;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.Scanner;

public class MakeTree {

    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);
        System.out.println("Enter test file name (i.e code/input.txt)");
        String filename = input.nextLine();
        createMerkleTree(filename);
        
    }

    public static Node createMerkleTree(String filename) throws Exception{
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        ArrayList<Node> nodes = new ArrayList<Node>();

        while ( sc.hasNextLine()){
            String str = sc.nextLine();
            String[] splited = str.split(" ");
            String address = splited[0];
            String balance = splited[1];
            String hash = getSHA(address + balance);
            Content curContent = new Content(hash, address, balance);
            Node curNode = new Node(true, curContent, null, null);

            nodes.add(curNode);
        }
        ArrayList<Node> merkleRoot = merkleTree(nodes);
        return merkleRoot.get(0);
    }


    private static ArrayList<Node> merkleTree(ArrayList<Node> children) throws Exception{
        // edge case of 1 child
        if (children.size() == 1)
            return children;
        ArrayList<Node> parentList = new ArrayList<>();
        // iterate 2 at time through children to make a parent
        for (int i = 0; i < children.size() -1; i += 2) {
            Node left = children.get(i);
            Node right = children.get(i + 1);
            String parentHash = getSHA(left.getContent().getHash().concat(right.getContent().getHash()));
            Content parentContent = new Content(parentHash, "", "");
            parentList.add(new Node(true, parentContent, left, right));
        }
        if (children.size() % 2 == 1) {
            Node last = children.get(children.size() - 1);
            String parentHash = last.getContent().getHash();
            Content parentContent = new Content(parentHash, "", "");
            parentList.add(new Node(true, parentContent, last, null));
        }
        return merkleTree(parentList);
    }

    public String printMerkleTree(Node root) throws Exception{
        String result = "";
        if (root == null){
            return result;
        }
        if (root.getLeft() == null && root.getRight() == null){
            result += (root.getContent().getAddress() + " " + root.getContent().getBalance()) ;
        }
        result += printMerkleTree(root.getLeft());
        result += printMerkleTree(root.getRight());
        return result;
    }

    public static String getSHA(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

}

//hw4

//Node.java
package code;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.management.RuntimeErrorException;

class Node {
    private Boolean isLeaf;
    private Content content;
    private Node left;
    private Node right;

    public Node(Boolean isLeaf, Content content, Node left, Node right){
        this.isLeaf = isLeaf;
        this.content = content;
        this.left = left;
        this.right = right;
    }
    public Boolean getIsLeaf(){
        return isLeaf;
    }
    public Content getContent(){
        return content;
    }
    public Node getLeft(){
        return left;
    }
    public Node getRight(){
        return right;
    }
    public String printMerkleTree(Node root) throws Exception{
        String result = "";
        if (root == null){
            return result;
        }
        if (root.getLeft() == null && root.getRight() == null){
            result += (root.getContent().getAddress() + " " + root.getContent().getBalance() + "\n") ;
        }
        result += printMerkleTree(root.getLeft());
        result += printMerkleTree(root.getRight());
        return result;
    }
}

//MakeBlock.java
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

//Block.java
package code;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.Scanner;

import javax.management.RuntimeErrorException;

public class Block {
    private String hashOfPrevBlockHeader;
    private String hashOfRoot;
    private int time;
    private BigInteger targetBoundary = new BigInteger("57896045000000000000000000000000000000000000000000000000000000000000000000000");
    private String nonce;
    private Node ledgerRoot;

    public Block(String hashOfPrevBlockHeader, String hashOfRoot, int time ,Node ledgerRoot) throws NoSuchAlgorithmException{
        this.hashOfPrevBlockHeader = hashOfPrevBlockHeader;
        this.hashOfRoot = hashOfRoot;
        this.time = time;
        //this.nonce = nonce;
        this.ledgerRoot = ledgerRoot;
        boolean nonceFound = true;
        while(nonceFound){
            String curNonce = getRandomNonce(8);
            String hashInput = hashOfRoot + curNonce;
            BigInteger hashOutput = getSHAint(hashInput);

            if ( this.targetBoundary.compareTo(hashOutput) >= 0 ){  //works 50 percent of time
                this.nonce = curNonce;
                nonceFound = false;
                break;
            }
        }

    }

    public String getHeaderHash() throws NoSuchAlgorithmException {
        String fullHeader = "";
        String timestring = String.valueOf(time);
        String boundaryString = String.valueOf(targetBoundary);
        fullHeader = hashOfPrevBlockHeader + hashOfRoot + timestring +  boundaryString + nonce;
        fullHeader = getSHA(fullHeader);
        return fullHeader;
    }

    public static String getRandomNonce(int length) { // http://www.java2s.com/example/java/java.util/get-random-nonce-by-length.html
        StringBuffer buffer = new StringBuffer( "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }



    public static String getSHA(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        //return no;
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
    public static BigInteger getSHAint(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no;
    }

    public String getHashPrevBlock() {
        return this.hashOfPrevBlockHeader;
    }
    public String getHashOfRoot() {
        return this.hashOfRoot;
    }
    public int getTime() {
        return this.time;
    }
    public BigInteger getBoundary() {
        return this.targetBoundary;
    }
    public String getNonce() {
        return this.nonce;
    }
    public Node getLedgerRoot() {
        return this.ledgerRoot;
    }

}
