/**
 * Adds to Content's hash attribute address, and balance
 */
public class LeafContent extends Content{
    private String address;
    private int balance;

    public LeafContent(String hash, String address, int balance){
        super(hash);
        this.address = address;
        this.balance = balance;
    }
    public String getAddress(){
        return address;
    }
    public int getBalance(){
        return balance;
    }   
}
