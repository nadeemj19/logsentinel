package com.logsentinel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogParserTest {
    private final LogParser parser = new LogParser();

    @Test
    void parsesFailedLogin() {
        LogEvent event = parser.parse("Oct 12 03:14:01 server sshd[1201]: Failed password for root from 203.0.113.7 port 52211 ssh2");

        assertEquals("root", event.user());
        assertEquals("203.0.113.7", event.ip());
        assertFalse(event.success());
    }

    @Test
    void parsesSuccessfulLogin() {
        LogEvent event = parser.parse("Oct 12 13:41:51 server sshd[1310]: Accepted password for bob from 198.51.100.22 port 50122 ssh2");
        assertEquals("bob", event.user());
        assertEquals("198.51.100.22", event.ip());
        assertTrue(event.success());
    }

    @Test
    void ignoresNonLoginLines() {
        LogEvent event = parser.parse(" Oct 12 14:44:12 server CRON[1400]: pam_unix(cron:session): session opened for user root");
        assertNull(event);
    }

}
