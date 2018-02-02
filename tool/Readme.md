This folder contains the source code implementation of CrossRec. In the class Runner.java, there is an example to execute one round of validation for fold-10, you can run the code directly to see how it works.Furthermore, it is possible to perform the same experiment with other folds by changing the corresponding parameters  in evaluation.properties. File ListOfFolds.txt provides you with a full list of the input information for all folds.
To play with runner on the set of 1.200 GitHub projects mentioned in the paper, you run the following command:

```
mvn exec:java -Dexec.mainClass="conferences.msr2018.CrossRec.Runner"
```
The experimental results for LibRec can be found in the following folder: https://github.com/MSR18-CROSSREC/MSR18-CROSSREC/tree/master/experimental\_results/LibRec


