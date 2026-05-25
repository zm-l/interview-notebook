# Interview Notebook

## ByteDance

### Q: How do you implement a thread-safe Singleton in Java?

**Company:** ByteDance

**Topics:** [Thread Safety and Synchronization, Design Patterns (Singleton), Java Memory Model and Visibility]

#### Thread-Safe Singleton Implementation in Java

##### Overview
A Singleton ensures only one instance of a class exists throughout the application lifetime. Thread-safety is critical in multi-threaded environments to prevent race conditions during instance creation.

##### Best Approach: Eager Initialization (Thread-Safe by Design)
```java
public class SingletonEager {
    private static final SingletonEager INSTANCE = new SingletonEager();
    
    private SingletonEager() {
        // Private constructor prevents instantiation
    }
    
    public static SingletonEager getInstance() {
        return INSTANCE;
    }
}
```
**Pros:** Simple, thread-safe, no synchronization overhead  
**Cons:** Instance created at class loading time regardless of usage

##### Alternative: Double-Checked Locking (Lazy Initialization)
```java
public class SingletonLazy {
    private static volatile SingletonLazy instance;
    
    private SingletonLazy() {}
    
    public static SingletonLazy getInstance() {
        if (instance == null) {  // First check (no lock)
            synchronized (SingletonLazy.class) {
                if (instance == null) {  // Second check (with lock)
                    instance = new SingletonLazy();
                }
            }
        }
        return instance;
    }
}
```
**Pros:** Lazy initialization, minimal synchronization cost  
**Cons:** More complex, requires `volatile` keyword (Java 1.5+), potential visibility issues

##### Recommended: Bill Pugh Singleton (Class Loader Initialization)
```java
public class SingletonClassLoader {
    private SingletonClassLoader() {}
    
    private static class SingletonHolder {
        private static final SingletonClassLoader INSTANCE = new SingletonClassLoader();
    }
    
    public static SingletonClassLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```
**Pros:** Thread-safe by JVM guarantee, lazy initialization, clean, no synchronization  
**Cons:** Slightly more code, requires understanding of class loader semantics

##### Alternative: Enum (Most Thread-Safe)
```java
public enum SingletonEnum {
    INSTANCE;
    
    private String data;
    
    public void setData(String data) {
        this.data = data;
    }
    
    public String getData() {
        return data;
    }
}

// Usage
SingletonEnum singleton = SingletonEnum.INSTANCE;
```
**Pros:** Thread-safe by design, serialization-safe, reflection-safe, clearest intent  
**Cons:** Less flexible, enum semantics may not align with all use cases

##### Key Considerations

###### Why `volatile` is Essential
Without `volatile` in double-checked locking, compiler optimizations could reorder memory writes, causing other threads to see a partially constructed object:
```java
// Without volatile - UNSAFE
private static SingletonLazy instance;

// With volatile - SAFE
private static volatile SingletonLazy instance;
```

###### Thread-Safety Guarantees
- **Eager:** JVM class loading is thread-safe
- **Class Loader:** Inner class initialization is thread-safe via class loader
- **Double-Checked:** Requires `volatile` and proper synchronization
- **Enum:** Enum instantiation is thread-safe by specification

##### Recommendation
**Use Enum for new code** (simplest, safest). For existing codebases, prefer **Class Loader pattern** for lazy initialization with guaranteed thread-safety.

## KyberLife

### Q: What are the key differences between stack and heap memory in terms of allocation, performance, lifetime, and use cases?

**Company:** KyberLife

**Topics:** [Memory Management, Performance Optimization, System Architecture]

##### Stack vs Heap Memory

###### Core Differences

**Stack:**
- **Allocation**: Automatic, contiguous memory allocation in LIFO (Last-In-First-Out) order
- **Speed**: O(1) allocation/deallocation via simple pointer increment/decrement
- **Lifetime**: Automatic cleanup when variables go out of scope
- **Size**: Limited, fixed size per thread (typically 1-8 MB)
- **Thread-safe**: Each thread has its own stack
- **Fragmentation**: No fragmentation

**Heap:**
- **Allocation**: Manual or garbage-collected, non-contiguous memory
- **Speed**: Slower due to complex allocation algorithms (e.g., free-list, buddy system)
- **Lifetime**: Manual deallocation required (C/C++) or garbage collected (Java, Python)
- **Size**: Larger, limited only by available system RAM
- **Thread-safe**: Requires synchronization for concurrent access
- **Fragmentation**: Susceptible to fragmentation over time

###### Visual Example

```cpp
void function() {
    int stack_var = 42;           // Allocated on stack, O(1)
    int* heap_ptr = new int(42);  // Pointer on stack, data on heap
    
    static int static_var = 10;   // Data segment (static memory)
}  // stack_var automatically freed, heap_ptr leaked unless deleted
```

###### Performance Implications

**Stack advantages:**
- CPU cache-friendly (contiguous memory)
- Deterministic latency (critical for trading systems, real-time)
- Zero allocation overhead

**Stack limitations:**
- Cannot return pointers to stack-allocated data
- Limited for large data structures
- Stack overflow risk with recursion

**Heap advantages:**
- Dynamic sizing
- Can persist beyond function scope
- Suitable for unknown-size allocations

**Heap limitations:**
- GC pauses or fragmentation
- Cache misses from scattered memory
- Allocation latency varies

###### Best Practices for High-Performance Systems

1. **Prefer stack** for fixed-size, short-lived data
2. **Use object pooling** to avoid repeated heap allocations
3. **Pre-allocate** critical data structures to prevent runtime allocation
4. **Monitor memory layout** in latency-sensitive systems (e.g., trading engines)
5. **Avoid pointer chasing** across fragmented heap memory

###### Real-World Example: Low-Latency Trading

```cpp
// High-performance order processing
struct Order {
    uint64_t id;
    double price;      // Stack: ~16 bytes
    int quantity;
};

void processOrder(const Order& order) {  // Pass by reference
    Order local_copy = order;  // Stack allocation - negligible cost
    // Process immediately - no heap allocation overhead
}

// Avoid this in hot path:
void slowProcess(Order* order) {
    Order* heap_copy = new Order(*order);  // Allocation latency
    // ... use heap_copy ...
    delete heap_copy;  // Deallocation latency
}
```

###### Memory Layout Summary

```
High Address ┌─────────────────┐
             │   Stack ↓       │  (grows downward)
             ├─────────────────┤
             │   (free space)  │
             ├─────────────────┤
             │   Heap ↑        │  (grows upward)
             ├─────────────────┤
             │   BSS/Data      │  (static, globals)
             ├─────────────────┤
             │   Code          │  (read-only)
Low Address  └─────────────────┘
```

###### Key Takeaway
Choose **stack** for performance-critical code, **heap** for dynamic memory needs. In ultra-low-latency systems, minimize both allocation calls and prefer pre-allocated, ring-buffer data structures.

