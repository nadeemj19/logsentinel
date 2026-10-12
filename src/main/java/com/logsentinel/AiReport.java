package com.logsentinel;

import java.util.List;

public record AiReport(String summary, List<AiFinding> findings) {

}
