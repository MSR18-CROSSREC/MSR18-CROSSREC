This folder contains the source code implementation of CrossRec. To run the code on the set of 1.200 GitHub projects mentioned in the paper, you need to do the following steps: 

1. Download the metadata from the following link: https://github.com/MSR18-CROSSREC/MSR18-CROSSREC/blob/master/Data/ExperimentData.zip

2. Change the parameters specified in evaluation.properties to meet your configurations. Let's say you create the following folder: /home/user/Evaluation/Dataset1200/CrossRec/

3. Unzip ExperimentData.zip to /home/user/Evaluation/Dataset1200/CrossRec/

4. Create ten sub-folders: Round1, Round2, ... Round10

5. In Runner.java, there is an example to execute one round of validation for fold-10. You can run the code.

6. You can perform the same experiment with other folds by changing the corresponding parameters. File ListOfFolds.txt contains all the necessary input information for all folds.




