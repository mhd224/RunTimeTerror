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
public class ValidateBlock {
    public static void main(String[] args) throws Exception{
        //ask user for input file
        Scanner input = new Scanner(System.in);        
        System.out.println("Please print file name that hold blockchain");
        String fileName = input.nextLine();
        ArrayList<Block> blocks = new ArrayList<Block>();
        if ( readBlockChain(fileName, blocks) == false){
            System.out.println("Invalid format. Blockchain could not be read");
            return;
        }
        System.out.println("Validating blockchain...");
        if ( validateChain(blocks) == false){
            System.out.println("Invalid blockcahin. Blockchain is invalid");
            return;
        }
        System.out.println("Blockcahin validated.");
        menuRoutine(input);        //do menu routine
    }
    public static boolean readBlockChain(String filename, ArrayList<Block> blocks ) throws Exception{
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        int blockCounter = 0;
        // System.out.println( sc.nextLine() ) ; //flush
        // System.out.println( sc.nextLine() ) ; //flush
        // System.out.println( sc.nextLine() ) ; //flush

        try {                   // need 8 lines of header
            while( sc.hasNext() ) {  // each loops makes 1 block
                String beginBlockLine = "";
                beginBlockLine= sc.nextLine();
                if (beginBlockLine == ""){  // empty 
                    continue;
                }
                if ( !beginBlockLine.equals("BEGIN BLOCK") ){
                    System.out.println("Header not formatted correctly at block " + blockCounter +  ". BEGIN BLOCK expected. Received " + beginBlockLine);
                    return false;
                }
                String beginHeaderLine = sc.nextLine();
                if ( !beginHeaderLine.equals("BEGIN HEADER") ){
                    System.out.println("Header not formatted correctly at block " + blockCounter +  ".  BEGIN HEADER expected. Received " + beginHeaderLine);
                    return false;
                }
                String hashOfPrevBlockHeader = sc.nextLine();
                String hashOfRoot = sc.nextLine();
                String time = sc.nextLine();
                String targetBounString = sc.nextLine();
                String nonce = sc.nextLine();
                String endHeaderLine = sc.nextLine();
                if ( !endHeaderLine.equals("END HEADER") ){
                    System.out.println("Header not formatted correctly at block " + blockCounter +  ". END HEADER expected. Received " + endHeaderLine);
                    return false;
                }
                ArrayList<Node> nodes = new ArrayList<Node>();
                while(true){
                    String curString = sc.nextLine();
                    if ( curString.equals("END BLOCK")){
                        break;
                    }
                    String[] splitted = curString.split(" ");
                    if (splitted.length != 2){
                        System.out.println("Invalid account entry");
                    }
                    String address = splitted[0];
                    String balance = splitted[1];
                    String hash = getSHA(address + balance);
                    Content curContent = new Content(hash, address, balance);
                    Node curNode = new Node(true, curContent, null, null);
                    nodes.add(curNode);
                }
                ArrayList<Node> merkleRoot = merkleTree(nodes);
                Node root = merkleRoot.get(0);  
                int timeAsInt = Integer.valueOf(time);
                Block newBlock = new Block(hashOfPrevBlockHeader, hashOfRoot, timeAsInt, root, new BigInteger(targetBounString), nonce);
                blocks.add(newBlock);
                blockCounter += 1;
            } 
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Block not formatted correclty");
            return false;
        }
        return true;
    }

    public static boolean validateChain(ArrayList<Block> blocks) throws Exception{
        String hashOfPrevBlockHeader = blocks.get(0).getHashOfRoot();
        for(Block curBlock: blocks){
            if (! curBlock.getHashPrevBlock().equals(hashOfPrevBlockHeader) ){    //if prevHash not matching false!
                return false;
            }
            if ( validateBlock(curBlock) == false){         //check if block is validated
                return false;
            }
            hashOfPrevBlockHeader = curBlock.getHashOfRoot();   //update prevHash
        }
        return true;
    }
    public static boolean validateBlock(Block b) throws Exception{
        if ( b.getLedgerRoot().getContent().getHash().equals(b.getHashOfRoot()) == false){  //check if root was made correclty
            System.out.println("Provided root is invalid for block @ time " + b.getTime());
            return false;
        }
        String hashInput = b.getHashOfRoot() + b.getNonce();
        BigInteger hashOutput = getSHAint(hashInput);
        if ( b.curTarget.compareTo(hashOutput) >= 0 ){          //check if nonce was correcl
            System.out.println("Nonce is invalid for block @ time " + b.getTime());
            return false;
        }
        return true;
    }

    public static ArrayList<String> proveMembership(String accountNum){
        // childNodeLeft = index * 2 
        // childNodeRight = index * 2 + 1
        ArrayList<String> hashes = new ArrayList<>();
        return hashes;
    }

    //Return true if member
    public static int getBalance(String accountNum){
        //prove membership
        //then return balance
        return 0;
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
    public static BigInteger getSHAint(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no;
    }

    public static void menuRoutine(Scanner sc){
        String input = "";
        int selection = 0;
        System.out.println("Select an Option Below:");
        System.out.println("[1] Proof of Membership");
        System.out.println("[2] Get Balance for Account");
        System.out.println("[3] Quit");
        System.out.print("Your Selection (1, 2, or 3): ");
        do {
            try {
                input = sc.nextLine();
                selection = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[-] Not a valid interface selection, please try again:");
                menuRoutine(sc);
            }
            if (selection != 1 && selection != 2 && selection != 3) {
                System.out.println("[-] Not a valid interface selection, please try again:");
                menuRoutine(sc);
            }
        } while (selection != 1 && selection != 2 && selection != 3);

        if (selection == 1) {   //proof of membership
            System.out.println("[+] insert code for proof of membership here");
            System.exit(0);
        } else if (selection == 2) { //get balance
            System.out.println("[+] insert code for get balance for account here");
            System.exit(0);
        } else if (selection == 3) {    //quit
            System.out.println("[+] Quitting program...");
            System.exit(0);
        }
    }
}
