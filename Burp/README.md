# Burp Suite Color Extension

This project contains the basic structure for a Burp Suite extension using the Montoya API.

## Prerequisites
- Java 17 or later
- Gradle (if not using the included wrapper - Note: Wrapper generation failed due to missing system Gradle)

## Setup
1. Use a system with Gradle installed to generate the wrapper:
   ```bash
   gradle wrapper
   ```
2. Build the project:
   ```bash
   ./gradlew build
   ```
3. Load the resulting JAR (in `build/libs`) into Burp Suite.
