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

