This folder contains the source code implementation of CrossRec. In Runner.java, there is an example to execute one round of validation for fold-10, you can run the code directly to see how it works.You can perform the same experiment with other folds by changing the corresponding parameters. File ListOfFolds.txt contains all the necessary input information for all folds.	 File ListOfFolds.txt provides you with a full list of the input information for all folds.
To play with runner on the set of 1.200 GitHub projects mentioned in the paper, you have to run:

```
mvn exec:java -Dexec.mainClass="conferences.msr2018.CrossRec.Runner"
```
You can perform the same experiment with other folds by changing the subFolder property in evaluation.properties 


