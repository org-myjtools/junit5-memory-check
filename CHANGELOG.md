# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-11

### Added

- `MemoryExtension` implementing `BeforeTestExecutionCallback` and `AfterTestExecutionCallback`
- Configurable memory threshold in bytes via constructor parameter
- Automatic garbage collection before and after each test for consistent measurements
- `AssertionError` thrown when memory consumption exceeds the configured limit
- Console output of memory used per test on success