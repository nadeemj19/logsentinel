package com.logsentinel;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StopReason;
import com.anthropic.models.messages.StructuredMessage;
import com.anthropic.models.messages.StructuredMessageCreateParams;

import java.util.List;

public class AiAnalyzer {
    private final AnthropicClient client = AnthropicOkHttpClient.fromEnv();

    public AiReport analyze(List<Alert> alerts) {
        String prompt = "You are a security analyst. These alerts came from an SSH log analyser. "
                + "For each alert, rate its severity (LOW, MEDIUM, HIGH or CRITICAL), "
                + "explain what it likely means in plain English, and give one concrete recommendation. "
                + "Also write a short overall summary.\n\nAlerts:\n";

        // my loop

        for (Alert alert : alerts) {
            prompt = prompt + "- " + alert.severity() + ": " + alert.message() + "\n";
        }

        // send to Claude and return the report

        StructuredMessageCreateParams<AiReport> params = MessageCreateParams.builder()
                .model("claude-opus-5-5")
                .maxTokens(16000L)
                .outputConfig(AiReport.class)
                .addUserMessage(prompt)
                .build();

        StructuredMessage<AiReport> response = client.messages().create(params);

        if (response.stopReason().isPresent() && response.stopReason().get().equals(StopReason.REFUSAL)) {
            throw new IllegalStateException("Claude declined to analyse these alerts");
        }

        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .orElseThrow()
                .text();


    }
}


