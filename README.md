This repository contains the source code implementation of CrossRec, the dataset as well as the experimental results for the following paper:

"A collaborative-filtering approach to provide OSS developers with third-party library recommendations"

submitted to the 15th International Conference on Mining Software Repositories (MSR 2018)

CrossRec is a framework that exploits cross projects relationships in open source software repositories to build a recommender system. It aims at supporting software developers who have already included some libraries in the new projects being developed, and expect to get recommendations on which additional libraries should be further incorporated.

CrossRec has been evaluated by considering different quality metrics and a dataset consisting of 1.200 GitHub Java projects. The performed evaluation demonstrated that our approach outperforms LibRec, a well-known system for library recommendation with regards to various quality indicators. To the best of our knowledge, our work is the first one that employs graphs to represent the relationships among software projects so as to effectively compute similarity and eventually to recommend libraries. 




