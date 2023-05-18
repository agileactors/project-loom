# Project Loom

Project Loom is intended to explore, incubate and deliver Java VM features and APIs built on top of them for the purpose
of supporting easy-to-use, high-throughput lightweight concurrency and new programming models on the Java platform.
It is part of JDK 19.

## Virtual Threads

### Why current model is an issue?

Currently Java uses the OS kernel threads and this causes the following limitations:
1. The implementation of the java.lang.Thread type is called platform thread. The problem with platform threads is that
   they are expensive from a lot of points of view. First, they are costly to create. Whenever a platform thread is
   made, the OS must allocate a large amount of memory (megabytes) in the stack to store the thread context, native, and
   Java call stacks.
2. Applications can serve millions of transactions, and it is not feasible for one jvm to accommodate the one Thread per
   operation model.
3. Synchronization doesn't come for free especially when io is involved.

With the current tools, Java offer the ability to develop the code in a reactive fashion (e.g. RxJava) but this has the
following drawbacks:
1. Debugging is a nightmare.
2. Error handling is not that obvious.
3. K(eep) I(u) S(imple) S(tupid) principle is difficult to apply since code becomes complex.

### How to use virtual threads
1. Use simple blocking I/O apis
2. Avoid Pinning (using synchronized blocks). This can lead to deadlocks (-Djdk.tracePinnedThreads to detect them)
3. Don't use ThreadLocal to cache objects
4. From shared thread pools -> new virtual thread per task

https://en.wikipedia.org/wiki/Little%27s_law