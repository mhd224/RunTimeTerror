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
