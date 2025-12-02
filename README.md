# Unit Testing Program Template Generator

This repository contains a **Unit Testing Generator** for Java classes. It allows you to automatically generate **base unit test program templates** from compiled `.class` files.

---

## Project Overview

- The project contains source code to build the **UTGenerator**.
- After building, the generator can create a **starter unit test template** for any Java class.
- The generated template includes basic structure for **TestNG tests**, ready for further development.

---

## Getting Started

### 1. Clone the Repository
### 2. Build UTGenerator JAR
### 3. Run UTGenerator

- java -jar ./UTGenerator_c.jar <path-to-class-file>

---

## Notes

- Make sure the `.class` file is **compiled** and accessible.
- Generated templates are **starting points** — you still need to add actual test logic.

---

## Dependencies

- Java JDK 17 or later
- TestNG (for the generated test templates)
