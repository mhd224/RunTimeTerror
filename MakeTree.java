package RunTimeTerror;

public class MakeTree {
    
}


// basically dfs
// inputs: list of nodes (leaves initially)
// make q from list of nodes
// int curLen = q.size();
// while q.size <= 1: // if q size is 1, we have our root  
//     currentSize = q.size(); //iterate through currsize because we will be adding to q as we make nodes
//     for(int i = 0; i <currentSize; i+= 2 ){
//         leaf1 = q[i]
//         leaf2;
//         IF i + 1< currentSize :   //might be out of bounds
//              leaf2 = q[i+1] 
//         else 
//              leaf2 = NUll;
//         get the hashes of both leaves (if leaf 2 == null, set second hash equal to  "")
//         make a new node (newNode), 
//         set newNode's hash to SHA256( leaf1.hash + leaf2.hash()) 
//         set newNode.left = leaf1
//         set newNode.right = leaf2 (if leaf2 != null)
//         append newNode to the RIGHT of quue 
