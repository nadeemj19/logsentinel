# LogSentinel

![Build](https://github.com/nadeemj19/logsentinel/actions/workflows/build.yml/badge.svg)

A command-line tool that scans Linux SSH logs for suspicious login activity and uses an LLM to explain each threat and recommend a fix.

## What it does
- Parses `auth.log`-style SSH logs into structured login events (any log file can be passed as an argument)
- Detects **successful root logins** (HIGH severity)
- Detects **brute-force attempts**: 3 or more failed logins from the same IP (MEDIUM severity)
- Sends the alerts to Claude, which returns a structured report with a plain-English explanation and a recommendation for each one
- Tested on 2,000 lines of real-world SSH logs from the [Loghub](https://github.com/logpai/loghub) dataset

## Example output (real-world data)
Run against `samples/OpenSSH_2k.log`, a real server's SSH log from Loghub:
```
Parsed 521 login events
Alert[severity=MEDIUM, message=Brute force 183.62.140.253 failed 286 times]
Alert[severity=MEDIUM, message=Brute force 187.141.143.180 failed 80 times]
Alert[severity=MEDIUM, message=Brute force 103.99.0.122 failed 46 times]
... (12 attacking IPs in total)

=== AI Analysis ===
The server's SSH service is being hit by typical internet-wide automated brute-force
activity from 12 IPs, totalling about 502 failed logins. Two sources stand out:
183.62.140.253 (286 failures) and 187.141.143.180 (80 failures)... Two addresses in
103.207.39.0/24 suggest coordinated scanning...
AiFinding[severity=HIGH, explanation=183.62.140.253 failed 286 times. This is by far the
most aggressive source: a persistent, high-volume brute-force campaign..., recommendation=
Block the IP at the perimeter now and audit all logins for this IP...]
AiFinding[severity=LOW, explanation=103.207.39.212 failed 3 times. This is low volume on its
own, but it comes from the same /24 block as 103.207.39.16, which points to coordinated
scanning..., recommendation=Block 103.207.39.0/24 as a range rather than individual IPs.]
```
*(Output trimmed.)* The AI spotted that two attacking IPs share a network block (likely one attacker), which per-IP rules can't detect.

Running with no argument uses `samples/auth.log`, a small hand-written log that also triggers the root-login rule.

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
2. Run against the real-world sample (or any SSH log file):
   ```
   mvn compile exec:java -Dexec.mainClass=com.logsentinel.Main -Dexec.args="samples/OpenSSH_2k.log"
   ```
   Leave out `-Dexec.args=...` to use the default `samples/auth.log`.

## Running the tests
```
mvn test
```
No API key is needed: the tests cover the parser and the detection rules, and never call the AI.

## Future improvements
- Scale severity with volume (e.g. 50+ failures = HIGH) instead of rating all brute force MEDIUM, as the AI itself suggested
- Use real timestamps so brute force means "N failures **within X minutes**", not N failures in total
- Support more log formats, such as web server access logs

## Data
`samples/OpenSSH_2k.log` is from [Loghub](https://github.com/logpai/loghub), a collection of real system logs published for research by the LogPAI team. `samples/auth.log` is a small hand-written sample.