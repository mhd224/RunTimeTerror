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
    static ArrayList<Node> treeNodes = new ArrayList<Node>();  // holds each block's merkle tree in BFS order
    static ArrayList<Block> blocks = new ArrayList<Block>();   // holds each block 
    public static void main(String[] args) throws Exception {
        // ask user for input file
        Scanner input = new Scanner(System.in);
        System.out.println("Please print file name that hold blockchain");
        String fileName = input.nextLine();
        
        
        if (readBlockChain(fileName) == false) {
            System.out.println("Invalid format. Blockchain could not be read");
            return;
        }
        System.out.println("Validating blockchain...");
        if (validateChain(blocks) == false) {
            System.out.println("Invalid blockcahin. Blockchain is invalid");
            return;
        }
        System.out.println("Blockcahin validated.");
        menuRoutine(input); // do menu routine
    }

    public static boolean readBlockChain(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        int blockCounter = 0;

        try { // need 8 lines of header
            while (sc.hasNext()) { // each loops makes 1 block
                String beginBlockLine = "";
                beginBlockLine = sc.nextLine();
                if (beginBlockLine == "") { // empty
                    continue;
                }
                if (!beginBlockLine.equals("BEGIN BLOCK")) {
                    System.out.println("Header not formatted correctly at block " + blockCounter
                            + ". BEGIN BLOCK expected. Received " + beginBlockLine);
                    return false;
                }
                String beginHeaderLine = sc.nextLine();
                if (!beginHeaderLine.equals("BEGIN HEADER")) {
                    System.out.println("Header not formatted correctly at block " + blockCounter
                            + ".  BEGIN HEADER expected. Received " + beginHeaderLine);
                    return false;
                }
                String hashOfPrevBlockHeader = sc.nextLine();
                String hashOfRoot = sc.nextLine();
                String time = sc.nextLine();
                String targetBounString = sc.nextLine();
                String nonce = sc.nextLine();
                String endHeaderLine = sc.nextLine();
                if (!endHeaderLine.equals("END HEADER")) {
                    System.out.println("Header not formatted correctly at block " + blockCounter
                            + ". END HEADER expected. Received " + endHeaderLine);
                    return false;
                }
                ArrayList<Node> nodes = new ArrayList<Node>();
                while (true) {
                    String curString = sc.nextLine();
                    if (curString.equals("END BLOCK")) {
                        if (sc.hasNextLine())
                            sc.nextLine();
                        break;
                    }
                    String[] splitted = curString.split(" ");
                    if (splitted.length != 2) {
                        System.out.println("Invalid account entry");
                    }
                    String address = splitted[0];
                    String balance = splitted[1];
                    String hash = getSHA(address + balance);
                    Content curContent = new Content(hash, address, balance);
                    Node curNode = new Node(true, curContent, null, null);
                    nodes.add(curNode);
                }
                ArrayList<Node> tree = merkleTree(nodes);
                Node root = tree.get(0);
                int timeAsInt = Integer.valueOf(time);
                Block newBlock = new Block(hashOfPrevBlockHeader, hashOfRoot, timeAsInt, root, new BigInteger(targetBounString), nonce);
                blocks.add(newBlock);  //holds each block
                treeNodes.add(root);   // holds each blocks tree
                System.out.println(printMerkleTree(root));
                blockCounter += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Block not formatted correclty");
            return false;
        }
        return true;
    }

    public static boolean validateChain(ArrayList<Block> blocks) throws Exception {
        // working backwards
        // blocks is in newest to oldest order
        String hashOfPrevBlockHeader = blocks.get(0).getHashPrevBlock();
        for (int i = 1; i < blocks.size(); i++) {
            Block curBlock = blocks.get(i);
            if (!curBlock.getHeaderHash().equals(hashOfPrevBlockHeader)) { // if prevHash not matching false!
                return false;
            }
            if (validateBlock(curBlock) == false) { // check if block is validated
                return false;
            }
            hashOfPrevBlockHeader = curBlock.getHeaderHash(); // update prevHash
        }
        return true;
    }

    public static boolean validateBlock(Block b) throws Exception {
        if (b.getLedgerRoot().getContent().getHash().equals(b.getHashOfRoot()) == false) { // check if root was made
                                                                                           // correclty
            System.out.println("Provided root is invalid for block @ time " + b.getTime());
            return false;
        }
        String hashInput = b.getHashOfRoot() + b.getNonce();
        BigInteger hashOutput = getSHAint(hashInput);

        if (b.curTarget.compareTo(hashOutput) <= 0) { // check if nonce was incorrect
            System.out.println("Nonce is invalid for block @ time " + b.getTime());
            return false;
        }
        return true;
    }

    public static void proveMembership(String address) throws Exception{  // ArrayList<String>
        int blockHoldingAddress = -1;
        for(int i = 0; i < blocks.size(); i++){      //go through each tree starting from trees[0]   (newest tree)
            Node curRoot = treeNodes.get(i);
            if ( findInTree(curRoot, address)){
                blockHoldingAddress = i;
                break;
            }
        }
        if(blockHoldingAddress == -1){
            System.out.println("Address not found.");
            //return hashes;
        }
        Node root = blocks.get(blockHoldingAddress).getLedgerRoot();
        ArrayList<Node> curPath = new ArrayList<Node>();
        curPath.add(root);
        ArrayList<Node> path = getPath(curPath, address);
        for(Node curNode : path){
            System.out.println(curNode.getContent().getAddress());
        }
        //return getSiblingPath(curPath, blockHoldingAddress);
    }

    public static ArrayList<Node> getPath(ArrayList<Node> curPath, String address){
        Node furthestNode = curPath.get( curPath.size() - 1);
        Node leftOption = furthestNode.getLeft();
        Node rightOption = furthestNode.getRight();

        if (furthestNode.getContent().getAddress() == address){
            return curPath;
        }
        if (leftOption != null){
            curPath.add(leftOption);
            getPath(curPath, address);
        }
        curPath.remove(furthestNode);  // left side failed
        if (rightOption != null){
            curPath.add(rightOption);
            getPath(curPath, address);
        }
        return curPath;  // never gets here
    }

    // Return true if member
    public static int getBalance(String accountNum) {
        // prove membership
        // then return balance
        return 0;
    }

    public static String getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private static ArrayList<Node> merkleTree(ArrayList<Node> children) throws Exception {
        // edge case of 1 child
        if (children.size() == 1)
            return children;
        ArrayList<Node> parentList = new ArrayList<>();
        // iterate 2 at time through children to make a parent
        for (int i = 0; i < children.size() - 1; i += 2) {
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

    public static BigInteger getSHAint(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no;
    }

    public static void menuRoutine(Scanner sc) throws Exception{
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

        if (selection == 1) { // proof of membership
            System.out.println("Enter address:");
            String address = sc.nextLine();
            proveMembership(address);
            System.exit(0);
        } else if (selection == 2) { // get balance
            System.out.println("[+] insert code for get balance for account here");
            System.exit(0);
        } else if (selection == 3) { // quit
            System.out.println("[+] Quitting program...");
            System.exit(0);
        }
    }
    public static boolean findInTree(Node root, String address) throws Exception{
        if (root == null){
            return false;
        }
        System.out.println("SEARCHING FOR ADDRESS:" + root.getContent().getAddress());
        if (root.getContent().getAddress().equals(address) ) {
            return true;
        }
        boolean bool1 = findInTree(root.getLeft(), address );
        boolean bool2 = findInTree(root.getRight(), address );
        return bool1 || bool2;
    }
    public static String printMerkleTree(Node root) throws Exception{
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
}
