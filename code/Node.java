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