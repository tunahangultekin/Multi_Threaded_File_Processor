# Multi-threaded File Processor

## Overview
A Java application demonstrating different multi-threading approaches for processing large text files. 
Compares performance between single-threaded, multi-threaded, and producer-consumer patterns.

## Features
- **Single-threaded Processing:** Baseline performance measurement
- **Multi-threaded Processing:** ExecutorService with configurable thread pool
- **Producer-Consumer Pattern:** BlockingQueue-based implementation
- **Performance Metrics:** Memory usage and processing time tracking
- **Generic Design:** Type-safe, reusable components


# PERFORMANCE COMPARISON:  


| Processing Method       | Time (ms) | Memory Usage   | Throughput   | Speedup Factor |
|-------------------------|-----------|----------------|--------------|----------------|
| Single-threaded         | 1,967 ms  | -42.48 MB      | 25.42 MB/s   | 1.00x (base)   |
| Multi-threaded (4)      | 599 ms    | +126.06 MB     | 83.47 MB/s   | 3.28x faster   |
| Producer-Consumer (4)   | 542 ms    | +255.24 MB     | 92.25 MB/s   | 3.63x faster   |

---

## DETAILED ANALYSIS  

### Single-threaded Processing  
- **Chunks processed:** 532  
- **Unique words found:** 9  
- **Processing time:** 1.967 seconds  
- **Memory efficiency:** Negative delta (Garbage Collection occurred)  
- **Per-thread throughput:** 25.42 MB/s  

---

###  Multi-threaded Processing (4 threads)  
- **Chunks processed:** 532  
- **Unique words found:** 9  
- **Processing time:** 0.599 seconds  
- **Memory overhead:** +126.06 MB  
- **Total throughput:** 83.47 MB/s  
- **Per-thread throughput:** 20.87 MB/s  
- **Performance gain:** 228% improvement  

---

###  Producer-Consumer Pattern (4 threads)  
- **Lines read by producer:** 531,615  
- **Chunks processed:** 532 (**100% success rate**)  
- **Failed chunks:** 0  
- **Processing time:** 0.542 seconds  
- **Average chunk processing time:** 3 ms  
- **Memory overhead:** +255.24 MB  
- **Total throughput:** 92.25 MB/s  
- **Per-thread throughput:** 23.06 MB/s  
- **Performance gain:** 263% improvement  
