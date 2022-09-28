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
    private BigInteger targetBoundary = BigInteger.valueOf( (long) Math.pow(2, 255) );
    private String nonce;
    private ArrayList<Node> ledger;

    public Block(String hashOfPrevBlockHeader, String hashOfRoot, int time ,ArrayList<Node> ledger) throws NoSuchAlgorithmException{
        this.hashOfPrevBlockHeader = hashOfPrevBlockHeader;
        this.hashOfRoot = hashOfRoot;
        this.time = time;
        this.nonce = nonce;
        while(true){
            String curNonce = getRandomNonce(8);
            String hashInput = hashOfRoot + curNonce;
            if ( this.targetBoundary.compareTo(getSHA(hashInput)) >= 0 ){  //works 50 percent of time
                this.nonce = curNonce;
                break;
            }
        }

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



    public static BigInteger getSHA(String input) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        BigInteger result = new BigInteger(hashtext);
        return result;
    }
}
