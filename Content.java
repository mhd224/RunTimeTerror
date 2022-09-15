/**
 * Use for non-lead nodes
 * Parent Class for LeafContent
 * 
 */
public class Content {
    static String hash;

    public Content(String hash){
        this.hash = hash;
    }
    public String getHash(){
        return hash;
    }
}
