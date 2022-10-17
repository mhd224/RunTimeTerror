package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        String fileName = "" ;
        Boolean bool = false;

        do {

            try{
                System.out.println("Please print file name that holds blockchain");
                fileName = input.nextLine();
    
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
                bool = true;
                menuRoutine(input); // do menu routine
    
    
            } catch (IOException e) {
                System.out.println("[-] Error, file was not found. Please try again...");
            }

        } while(bool != true);
        
    }

    //Helper function for reading a block chain
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

    //Helper function for validating a chain
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

    //Helper function for validating a block
    public static boolean validateBlock(Block b) throws Exception {
        if (b.getLedgerRoot().getContent().getHash().equals(b.getHashOfRoot()) == false) { // check if root was made                                                                                         
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

    //Helper function for proving membership
    public static ArrayList<String> proveMembership(String address) throws Exception{  // ArrayList<String>
        ArrayList<Node> curPath = new ArrayList<Node>();
        for(int i = 0; i < blocks.size(); i++){      //go through each tree starting from trees[0]   (newest tree)
            Node curRoot = treeNodes.get(i);
            if ( getTreePath(curRoot, curPath, address)) {
                System.out.println("path length " + curPath.size());
                for( Node curNode : curPath){
                    System.out.println(curNode.getContent().getHash());  //matches output
                }
                return getFullPath(curPath, i);
            }
            curPath.clear();
        }
        return null;
    }

    //Helper function for getTreePath
    public static boolean getTreePath(Node root, ArrayList<Node> curPath, String address){
        if (root == null){
            return false;
        }
        curPath.add(root);
        if (root.getContent().getAddress().equals(address) ){
            return true;
        }
        if( getTreePath(root.getLeft(), curPath, address) || getTreePath(root.getRight(), curPath, address)){
            return true;
        }
        curPath.remove(root);
        return false;
    }

    //Helper function for getFullPath
    public static ArrayList<String> getFullPath(ArrayList<Node> curPath, int indexInBlock) throws Exception{
        ArrayList<String> res = new ArrayList<String>();
        Node parent = curPath.get(0);
        res.add("HASH-ROOT: " + parent.getContent().getHash());
        Node child1; Node child2;
        int count = 0;
        for(int i = 1; i < curPath.size() - 1; i+= 1){
            child1 = parent.getLeft();
            child2 = parent.getRight();
            String child2Hash = "null";
            String child1Hash = "null";
            if (child2 != null)
                child2Hash = child2.getContent().getHash();
            if (child1 != null)
                child1Hash = child1.getContent().getHash();
            if(child1 == curPath.get(i+1)){     //add searched for account LAST
                String str = "SIBLING PAIR " + String.valueOf(count++) + ": " + child2Hash + " " + child1Hash;
                res.add(str);
            }
            else{
                String str = "SIBLING PAIR " + String.valueOf(count++) + ": " + child1Hash + " " + child2Hash;
                res.add(str);
            }
            parent = curPath.get(i+1);
        }
        Collections.reverse(res);  //start from leaf, work way up
        res.add("HEADER BEGIN");
        res.add("Hash of previous block "+ blocks.get(indexInBlock).getHashPrevBlock()) ;
        res.add("Hash of root " + blocks.get(indexInBlock).getHashOfRoot());
        res.add("Time  " + String.valueOf(blocks.get(indexInBlock).getTime()) );
        res.add("Difficulty target "+ blocks.get(indexInBlock).curTarget);
        res.add("Nonce "+ blocks.get(indexInBlock).getNonce() );
        res.add("HEADER END");
        res.add("BLOCK HASHES BEGIN");
        for(int i = indexInBlock; i >= 0; i--){  // get hash of all blocks
            res.add(blocks.get(i).getHeaderHash() ) ;
        }
        res.add("BLOCK HASHES END");
        return res;
    }

    //Helper function for getting the balance  (overloaded)
    // Return true if member
    public static String getBalance(String address) {  
        StringBuilder balance = new StringBuilder ();
        for(int i = 0; i < blocks.size(); i++){      //go through each tree starting from trees[0]   (newest tree)
            Node curRoot = treeNodes.get(i);
            getBalance(curRoot, address, balance);
            if (balance.length() != 0) {
                return balance.toString();
            }
        }
        return "";
    }

    //Helper function for getting the balance  (overloaded)
    public static void getBalance(Node root,String address, StringBuilder balance){
        if (root == null){
            return;
        }
        if (root.getContent().getAddress().equals(address) ){
            balance.append(root.getContent().getBalance());
        }
        getBalance(root.getLeft(), address, balance);  
        getBalance(root.getRight(), address, balance);
    }

    //helper function for getting SHA hash test
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

    //helper function for merkleTree
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

    //Helper function for getting the SHA big integer
    public static BigInteger getSHAint(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no;
    }

    //Helper function for printing out the Menu Routine
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
            ArrayList<String> res = proveMembership(address);
            if (res == null){
                System.out.println("Address not found");
            }
            else{
                System.out.println("Proof of membership: ");
                for(String s: res){
                    System.out.println(s);
                }
            }
            menuRoutine(sc);
        } else if (selection == 2) { // get balance
            System.out.println("Enter address:");
            String address = sc.nextLine();
            ArrayList<String> res = proveMembership(address);
            if (res == null){
                System.out.println("Address not found");
            }
            else{
                System.out.println("Balance: " + getBalance(address));
                System.out.println("Proof of membership: ");
                for(String s: res){
                    System.out.println(s);
                }
            }
            menuRoutine(sc);
        } else if (selection == 3) { // quit
            System.out.println("[+] Quitting program...");
            System.exit(0);
        }
    }

    //Helper function to print out a Merkle Tree
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
