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
    //private BigInteger targetBoundary = BigInteger.valueOf((long)Math.pow(2, 255));
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
            //BigInteger result = new BigInteger(hashOutput.getBytes());
            System.out.println(targetBoundary + " " + hashOutput);

            if ( this.targetBoundary.compareTo(hashOutput) >= 0 ){  //works 50 percent of time
                this.nonce = curNonce;
                nonceFound = false;
                System.out.println("Nonce found!");
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

    public void printNoBody(String fileName){
    

    }
    public void printWithBody(String fileName){

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
