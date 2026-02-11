junit5-memory-check
=================================================================================================

[![Maven Central](https://img.shields.io/maven-central/v/org.myjtools/junit5-memory-check)](https://central.sonatype.com/artifact/org.myjtools/junit5-memory-check)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org-myjtools_junit5-memory-check&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=org-myjtools_junit5-memory-check)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org-myjtools_junit5-memory-check&metric=coverage)](https://sonarcloud.io/summary/new_code?id=org-myjtools_junit5-memory-check)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=org-myjtools_junit5-memory-check&metric=bugs)](https://sonarcloud.io/summary/new_code?id=org-myjtools_junit5-memory-check)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=org-myjtools_junit5-memory-check&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=org-myjtools_junit5-memory-check)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org-myjtools_junit5-memory-check&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=org-myjtools_junit5-memory-check)

A lightweight JUnit 5 extension that monitors heap memory consumption during test execution and
fails the test if it exceeds a configurable threshold.

## Requirements

- Java 11+
- JUnit 5.9+

## Installation

### Maven

```xml
<dependency>
    <groupId>org.myjtools</groupId>
    <artifactId>junit5-memory-check</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
testImplementation 'org.myjtools:junit5-memory-check:1.0.0'
```

## Usage

Register the extension in your test class using `@RegisterExtension`, specifying the maximum
number of bytes allowed per test:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.myjtools.junit5.memorycheck.MemoryExtension;

class MyTest {

    @RegisterExtension
    MemoryExtension memoryCheck = new MemoryExtension(5_000_000); // 5 MB limit

    @Test
    void myOperation() {
        // If this test consumes more than 5 MB of heap memory,
        // it will fail with an AssertionError
    }
}
```

If the memory consumed during the test exceeds the configured limit, the extension throws an
`AssertionError` with a message like:

```
Memory exceeded: 7340032 > 5000000
```

## How it works

1. **Before each test**: triggers garbage collection, waits briefly for it to complete, and
   records the current heap usage.
2. **After each test**: triggers garbage collection again, waits, and measures the new heap
   usage. If the difference exceeds the configured `maxBytes`, the test fails.
3. On success, prints the memory consumed to standard output.

> **Note**: The measurement is based on `Runtime.totalMemory() - Runtime.freeMemory()` and
> relies on `System.gc()`, which is a *hint* to the JVM. Results may vary between runs and JVM
> implementations. This extension is intended as a development aid, not a precise profiling tool.

## License

[MIT License](http://repository.jboss.org/licenses/mit.txt)
