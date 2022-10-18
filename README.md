# RunTimeTerror
### Connor Lachman, Nate Dean, Muhammad Delen, Henry Eaton
## Compile and Run:

code can be recompiled each time by executing the command:
```
sh recompile.sh
```

the code can be compiled, and run in the same step by executing the command:
```
sh run.sh
```

## Usage Instructions for Homework 5
- compile and run the code using the two commands above
- enter test file names (there are n provided test file names labelled h5input[1-n].txt or h5input[1-n]fail.txt)
    - NOTE: the test files are located within the code/ directory, keep that in mind when entering the file names into the program (USE: code/h5input[1-5].txt)
    - enter a single file
- when running non-fail files, use the menu to perform Proof of Membership or Get Balance operations
- all output will be in terminal/shell

### Testing Failed Inputs
- h5input3fail.txt: Changed hash of previous head
- h5input4fail.txt: Changed hash of previous head
- h5input5fail.txt: Changed the root of any block
- h5input6fail.txt: Changed the nonce of any block

## Proof of Membership Notes
- Program returns the sibling pairs starting from the leaf to the root and “provides the header and hash of that block and the block hashes forward in time from that block to the most recent block”
- Pair1 corresponds to the primary node on path, Pair2 corresponds to the sibling
- 	For null nodes, hash of parent equals hash of non-null sibling (no rehashing)

## Usage Instructions for Homework 4
- compile and run the code using the two commands above
- enter test file names (there are 5 provided test file names labelled input[1-5].txt)
    - NOTE: input.txt is the test file used for homework 3. Please ignore this file and use input files 1-5 for testing homework 4
    - NOTE: the test files are located within the code/ directory, keep that in mind when entering the file names into the program (USE: code/input[1-5].txt)
    - enter a single file, multiple file, or all of them depending on your desired result
    - when you are finished entering files, press ENTER to finish (the number of desired files to be read are displayed)
- enter 'y' or 'n' when prompted if you would like to print the ledger or not. (YES = y / NO = n)
- to view the result, open up 'input1.block.out' located in the code/ directory to view the list of blocks beginning with the last block (in required print format) and displays the complete address/balance list
    - NOTE: if you decide to use your own input testing files, the name of the outputted file will be in the format 'FIRST_INPUT_FILE_NAME.block.out' instead of 'input1.block.out'




## SOURCES:

1. Making a Merkle Tree in Java: https://medium.com/@vinayprabhu19/merkel-tree-in-java-b45093c8c6bd
2. MessageDigest Docs: https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
3. Merkle Tree implementation: https://www.pranaybathini.com/2021/05/merkle-tree.html

