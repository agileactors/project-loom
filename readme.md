# Project Loom

### Goal

Project Loom is intended to explore, incubate and deliver Java VM features and APIs built on top of them for the purpose of supporting easy-to-use, high-throughput lightweight concurrency and new programming models on the Java platform. 
It is part of JDK 19.

### Why current model is an issue?

Currently Java uses the OS kernel threads and this causes the following limitations:
1. Applications can serve millions of transactions, and it is not feasible for one jvm to accommodate the one Thread per operation model.
2. Synchronization doesn't come for free especially when io is involved.

With the current tools, Java offer the ability to develop the code in a reactive fashion (e.g. RxJava) but this has the following drawbacks:
1. Debugging is a nightmare.
2. Error handling is not that obvious.
3. K(eep) I(u) S(imple) S(tupid) principle is difficult to apply since code becomes complex.

