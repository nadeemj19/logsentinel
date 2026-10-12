# LogSentinel

![Build](https://github.com/nadeemj19/logsentinel/actions/workflows/build.yml/badge.svg)

A command-line tool that scans Linux SSH logs for suspicious login activity and uses an LLM to explain each threat and recommend a fix.

## What it does
- Parses `auth.log`-style SSH logs into structured login events
- Detects **successful root logins** (HIGH severity)
- Detects **brute-force attempts**: 3 or more failed logins from the same IP (MEDIUM severity)
- Sends the alerts to Claude, which returns a structured report with a plain-English explanation and a recommendation for each one

## Example output
```
Parsed 6 login events
Alert[severity=HIGH, message=Root login from 192.0.2.50]
Alert[severity=MEDIUM, message=Brute force 203.0.113.7 failed 4 times]

=== AI Analysis ===
Two SSH alerts were reviewed. The most important is a successful direct root login from
192.0.2.50: it is HIGH severity and should be verified as authorised right away...
AiFinding[severity=HIGH, explanation=Someone successfully logged in over SSH directly as
the root (administrator) account..., recommendation=Immediately verify with your
administrators whether this login was authorised... set 'PermitRootLogin no'...]
AiFinding[severity=MEDIUM, explanation=IP 203.0.113.7 failed to authenticate 4 times...,
recommendation=...block it and deploy rate limiting such as fail2ban...]
```
*(Output trimmed. The sample log uses reserved documentation IP ranges.)*

## How it works
1. **`LogParser`** reads each log line and extracts the time, user, IP and whether the login succeeded. Lines that aren't SSH logins are skipped.
2. **`Detector`** applies rule-based checks across all events and raises `Alert`s. It uses a `HashMap` to count failed logins per IP.
3. **`AiAnalyzer`** sends the alerts to the Claude API and uses structured output to map the response directly onto Java records (`AiReport`, `AiFinding`).

**Design decision:** detection is done with plain Java rules, not the AI. LLMs can produce confident but incorrect answers, so the rules make detection predictable and testable. The AI is only used to *explain* alerts that the rules have already found.

## Tech stack
- Java 25, Maven
- JUnit 5
- Claude API (`anthropic-java` SDK)
- GitHub Actions (CI)

## Running it
**Requirements:** Java 25, Maven and an Anthropic API key.

1. Set your API key as an environment variable called `ANTHROPIC_API_KEY`
2. Run:
   ```
   mvn compile exec:java -Dexec.mainClass=com.logsentinel.Main
   ```

## Running the tests
```
mvn test
```
No API key is needed: the tests cover the parser and the detection rules, and never call the AI.

## Future improvements
- Use real timestamps so brute force means "N failures **within X minutes**", not N failures in total
- Run against real-world datasets (e.g. the Loghub OpenSSH logs)
- Support more log formats, such as web server access logs
- Accept the log file path as a command-line argument