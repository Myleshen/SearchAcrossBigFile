# Search Across a Big File

## Problem Description

- Given a Big Text File, find all the occurrences of the given search strings in the file
- The search strings are case-sensitive as they are Commonly found Names

## Approach Taken

1. Batching the lines as they are read from the file
2. Creating Worker Threads for each batch
3. Each batch will find all the given search strings in that batch
4. Aggregating the results of all the batches
5. Writing the output to the console (And also a file for debugging purposes)

## Running the Program

1. The file "big.txt" is available in the resources folder which was got from [Big Text File](https://norvig/big.txt)
2. MAX_BATCH_SIZE is changeable by passing the required batch size as the first parameter when running the program
3. To pass a new file to the program, pass it as the second parameter when running the program (Use the full context path when providing a file that is not in the class path)
4. Use any IDE to run the program or use maven in the command line to run the program

```shell
mvn clean compile exec:java -Dexec.args="1000 src/main/resources/big.txt"

java -jar target/TextFinding-1.0-SNAPSHOT.jar 1000 "src/main/resources/big.txt"
```
