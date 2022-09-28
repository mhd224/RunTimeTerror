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
    //String result = "";

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
        //printMerkleTree(merkleRoot.get(0));
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
            //System.out.println(root.getContent().getHash());
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
