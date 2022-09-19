
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
//package RunTimeTerror;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class MakeTree {
    public static void main(String[] args) throws Exception{
        File file = new File("input.txt");
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
        // for(int i = 0 ; i < nodes.size(); i ++ ){
        //     System.out.print(i + " ");
        //     System.out.println(nodes.get(i).getContent().getBalance());
        // }
        createMerkleTree(nodes);
    }


    public static Node createMerkleTree(ArrayList<Node> children) throws Exception{
        ArrayList<Node> merkleRoot = merkleTree(children);
        printMerkleTree(merkleRoot.get(0));
        return merkleRoot.get(0);
    }
    public static void printMerkleTree(Node root) throws Exception{
        if (root == null){
            return;
        }
        if (root.getLeft() == null && root.getRight() == null){
            //System.out.println(root.getContent().getHash());
            System.out.println(root.getContent().getAddress() + " " + root.getContent().getBalance() ) ;
        }
        printMerkleTree(root.getLeft());
        printMerkleTree(root.getRight());
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


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Node{
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
}