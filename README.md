# Java Web Crawler

A simple recursive web crawler that extracts headings and links from websites, respects depth limits, filters by allowed domains, and generates a Markdown report.

---

## Features

- Crawl websites starting from a given URL
- Limit crawling by depth
- Restrict crawling to specific domains
- Extract and structure headings (`h1`–`h6`)
- Collect and recursively follow links
- Avoid visiting the same URL multiple times
- Detect and highlight broken links
- Generate a Markdown report (`crawl-report.md`)

---

## Requirements

- Java 25
- Gradle

---

## Build

Build the executable JAR using Gradle Shadow plugin:

```bash
./gradlew shadowJar
```

This will generate in `/build/libs`:

```
crawler.jar
```

---

## Run

Run the crawler from the command line:
```bash
java -jar crawler.jar <URL> <DEPTH> <DOMAIN_1> <DOMAIN_2> ...
```
## Example
```bash
java -jar crawler.jar https://aau.at 2 aau.at
```
`Beware, this example will crawl around 450 links as of 2026-04-28!`

## Arguments
- \<URL> - starting page to crawl (must be http/https)
- \<DEPTH> - maximum crawl depth (0 = only start page)
- \<DOMAINS> - one or more allowed domains

## Output

After execution, a file is generated:
```
crawl-report.md
```

---

## Test

Run all tests:
```bash
./gradlew clean test
```

Test report:
```path
build/reports/tests/test/index.html
```
