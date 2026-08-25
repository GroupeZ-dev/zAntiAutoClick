# Changelog

# [1.0.4] - 2026-08-25

- Added faststats metrics
- Fixed load errors
- Updated to Sarah 1.23

## [1.0.3] - 2026-02-23

### Fixed
- Fixed NPE when player disconnects during session analysis
- Fixed race condition causing duplicate session processing
- Fixed O(n²) queries in StorageManager - now uses O(1) lookups

### Performance
- Optimized ClickAnalyzer: reduced iterations from 7 to 2 passes
- Added automatic cleanup task for orphaned sessions (prevents memory leaks)

### Improved
- Added configuration validation with automatic correction of invalid values
- Better error logging for inventory loading failures
- Marked synchronous `select()` method as deprecated

### Refactored
- Extracted helper methods in StorageManager (`createSessionFromDTO`, `buildInvalidSessionIndex`)
- Simplified GlobalDatabaseConfiguration with helper methods
- Removed test code from production (`main()`, `look()`, `parse()` in ClickAnalyzer)

### Documentation
- Added `CLAUDE.md` for Claude Code instances
- Added Javadoc for SessionDTO explaining default return values
