package com.digitalheroes.golfcharity;

import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import com.digitalheroes.golfcharity.subscription.Subscription;
import com.digitalheroes.golfcharity.subscription.SubscriptionRepository;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlatformIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private SubscriptionRepository subscriptionRepository;

    @Test
    void authRegisterAndLoginFlowShouldWork() throws Exception {
        String email = "user-auth@example.com";

        String registerBody = """
                {
                  "fullName": "Auth User",
                  "email": "%s",
                  "password": "StrongPass!123"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"));

        String loginBody = """
                {
                  "email": "%s",
                  "password": "StrongPass!123"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void scoreServiceShouldKeepOnlyLatestFiveScores() throws Exception {
        String token = registerAndGetToken("score-user@example.com");

        for (int i = 1; i <= 6; i++) {
            String scoreBody = """
                    {
                      "scoreValue": %d,
                      "scoreDate": "%s"
                    }
                    """.formatted(10 + i, LocalDate.of(2026, 1, i + 1));

            mockMvc.perform(post("/api/v1/scores")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(scoreBody))
                    .andExpect(status().isOk());
        }

        MvcResult result = mockMvc.perform(get("/api/v1/scores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode scores = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(scores).hasSize(5);

        boolean hasOldestScore = false;
        for (JsonNode score : scores) {
            if (score.get("scoreValue").asInt() == 11) {
                hasOldestScore = true;
                break;
            }
        }
        assertThat(hasOldestScore).isFalse();
    }

    @Test
    void adminShouldExecuteDrawSuccessfully() throws Exception {
        String userToken = registerAndGetToken("draw-user@example.com");

        mockMvc.perform(post("/api/v1/subscriptions/activate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plan\":\"MONTHLY\"}"))
                .andExpect(status().isOk());

        for (int i = 0; i < 5; i++) {
            String scoreBody = """
                    {
                      "scoreValue": %d,
                      "scoreDate": "%s"
                    }
                    """.formatted(20 + i, LocalDate.of(2026, 2, i + 1));

            mockMvc.perform(post("/api/v1/scores")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(scoreBody))
                    .andExpect(status().isOk());
        }

        String adminToken = loginAndGetToken("admin@golfcharity.com", "Admin@12345");

        String drawBody = """
                {
                  "monthKey": "2099-12",
                  "mode": "RANDOM",
                  "publish": true
                }
                """;

        mockMvc.perform(post("/api/v1/draws/admin/execute")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(drawBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthKey").value("2099-12"))
                .andExpect(jsonPath("$.mode").value("RANDOM"))
                .andExpect(jsonPath("$.winningNumbers").isArray())
                .andExpect(jsonPath("$.winningNumbers.length()").value(5));
    }

                @Test
                void stripeWebhookShouldActivateAndUpdateSubscriptionStatus() throws Exception {
                                String email = "webhook-user@example.com";
                                registerAndGetToken(email);

                                String checkoutPayload = """
                                                                {
                                                                        "id": "evt_checkout_completed_1",
                                                                        "object": "event",
                                                                        "api_version": "2024-06-20",
                                                                        "type": "checkout.session.completed",
                                                                        "data": {
                                                                                "object": {
                                                                                        "id": "cs_test_123",
                                                                                        "object": "checkout.session",
                                                                                        "customer": "cus_123",
                                                                                        "subscription": "sub_123",
                                                                                        "customer_email": "%s",
                                                                                        "customer_details": {
                                                                                                "email": "%s"
                                                                                        },
                                                                                        "metadata": {
                                                                                                "plan": "YEARLY"
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                                """.formatted(email, email);

                                mockMvc.perform(post("/api/v1/subscriptions/stripe/webhook")
                                                                                                .header("Stripe-Signature", stripeSignature(checkoutPayload, "whsec_dummy"))
                                                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                                                .content(checkoutPayload))
                                                                .andExpect(status().isNoContent());

                                User user = userRepository.findByEmail(email).orElseThrow();
                                Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElseThrow();
                                assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
                                assertThat(subscription.getPlan()).isEqualTo(SubscriptionPlan.YEARLY);
                                assertThat(subscription.getProviderCustomerId()).isEqualTo("cus_123");
                                assertThat(subscription.getProviderSubscriptionId()).isEqualTo("sub_123");

                                String subscriptionUpdatedPayload = """
                                                                {
                                                                        "id": "evt_subscription_updated_1",
                                                                        "object": "event",
                                                                        "api_version": "2024-06-20",
                                                                        "type": "customer.subscription.updated",
                                                                        "data": {
                                                                                "object": {
                                                                                        "id": "sub_123",
                                                                                        "object": "subscription",
                                                                                        "customer": "cus_123",
                                                                                        "status": "canceled"
                                                                                }
                                                                        }
                                                                }
                                                                """;

                                mockMvc.perform(post("/api/v1/subscriptions/stripe/webhook")
                                                                                                .header("Stripe-Signature", stripeSignature(subscriptionUpdatedPayload, "whsec_dummy"))
                                                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                                                .content(subscriptionUpdatedPayload))
                                                                .andExpect(status().isNoContent());

                                Subscription updated = subscriptionRepository.findByUserId(user.getId()).orElseThrow();
                                assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.CANCELED);
                }

                @Test
                void openApiDocsShouldBeAccessibleWithoutAuthentication() throws Exception {
                                mockMvc.perform(get("/v3/api-docs"))
                                                                .andExpect(status().isOk())
                                                                .andExpect(jsonPath("$.openapi").value("3.0.1"));
                }

    private String registerAndGetToken(String email) throws Exception {
        String registerBody = """
                {
                  "fullName": "Test User",
                  "email": "%s",
                  "password": "StrongPass!123"
                }
                """.formatted(email);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String loginBody = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return json.get("token").asText();
    }

        private String stripeSignature(String payload, String secret) throws Exception {
                long timestamp = System.currentTimeMillis() / 1000;
                String signedPayload = timestamp + "." + payload;

                Mac sha256Hmac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
                sha256Hmac.init(secretKey);
                byte[] signatureBytes = sha256Hmac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
                String signature = HexFormat.of().formatHex(signatureBytes);

                return "t=" + timestamp + ",v1=" + signature;
        }
}
