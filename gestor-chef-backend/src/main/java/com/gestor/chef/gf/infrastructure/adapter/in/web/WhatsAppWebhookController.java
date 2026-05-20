package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestor.chef.gf.domain.port.in.OrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Slf4j
@RestController
@RequestMapping("/webhook/whatsapp")
@RequiredArgsConstructor
@Tag(
    name = "WhatsApp Webhook",
    description = "Endpoints publicos para la integracion con WhatsApp Business Cloud API (Meta). " +
                  "No requieren JWT. El GET verifica el webhook ante Meta; el POST recibe mensajes entrantes."
)
public class WhatsAppWebhookController {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SIGNATURE_PREFIX = "sha256=";

    private final OrderUseCase orderUseCase;
    private final ObjectMapper objectMapper;

    @Value("${app.whatsapp.verify-token:gestor-chef-webhook-token}")
    private String verifyToken;

    @Value("${app.whatsapp.app-secret:}")
    private String appSecret;

    @GetMapping
    @Operation(
        summary = "Verificacion del webhook por Meta",
        description = "Meta llama a este endpoint al registrar el webhook. " +
                      "Valida hub.verify_token y devuelve hub.challenge como texto plano. " +
                      "Retorna 403 si el token no coincide. Endpoint publico, no requiere JWT."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verificacion exitosa — devuelve hub.challenge"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token de verificacion incorrecto")
    })
    public ResponseEntity<String> verify(
            @Parameter(description = "Debe ser 'subscribe'") @RequestParam("hub.mode") String mode,
            @Parameter(description = "Token secreto configurado") @RequestParam("hub.verify_token") String token,
            @Parameter(description = "Valor a devolver sin cambios") @RequestParam("hub.challenge") String challenge) {
        log.info("[WhatsApp Webhook] Solicitud de verificacion recibida. mode={}", mode);
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("[WhatsApp Webhook] Verificacion exitosa");
            return ResponseEntity.ok(challenge);
        }
        log.warn("[WhatsApp Webhook] Verificacion fallida — token no coincide.");
        return ResponseEntity.status(403).body("Forbidden");
    }

    @PostMapping
    @Operation(
        summary = "Recepcion de mensajes entrantes de WhatsApp",
        description = "Meta envia eventos de mensajes a este endpoint. " +
                      "Solo se procesan mensajes de tipo 'text'. " +
                      "Por cada mensaje se crea una orden via OrderService.processWhatsAppOrder() " +
                      "y se envia confirmacion al cliente por WhatsApp. " +
                      "Siempre retorna 200 OK para evitar reintentos de Meta. " +
                      "Endpoint publico, no requiere JWT."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payload recibido y procesado o ignorado si no es texto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Firma invalida")
    })
    public ResponseEntity<Void> receive(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
        if (!isValidSignature(rawPayload, signature)) {
            log.warn("[WhatsApp Webhook] Firma invalida o ausente");
            return ResponseEntity.status(403).build();
        }
        try {
            processPayload(objectMapper.readTree(rawPayload));
        } catch (Exception e) {
            log.error("[WhatsApp Webhook] Error procesando payload: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    private boolean isValidSignature(String rawPayload, String signature) {
        if (appSecret == null || appSecret.isBlank()) {
            return true;
        }
        if (signature == null || !signature.startsWith(SIGNATURE_PREFIX)) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            String expected = SIGNATURE_PREFIX + HexFormat.of().formatHex(mac.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8)));
            return MessageDigestSafeEquals.equals(expected, signature);
        } catch (Exception e) {
            log.warn("[WhatsApp Webhook] No se pudo validar la firma: {}", e.getMessage());
            return false;
        }
    }

    private void processPayload(JsonNode payload) {
        JsonNode entries = payload.path("entry");
        if (!entries.isArray()) {
            return;
        }
        for (JsonNode entry : entries) {
            processEntry(entry);
        }
    }

    private void processEntry(JsonNode entry) {
        JsonNode changes = entry.path("changes");
        if (!changes.isArray()) {
            return;
        }
        for (JsonNode change : changes) {
            JsonNode messages = change.path("value").path("messages");
            if (messages.isArray()) {
                messages.forEach(this::processMessage);
            }
        }
    }

    private void processMessage(JsonNode message) {
        String type = message.path("type").asText();
        if (!"text".equals(type)) {
            log.debug("[WhatsApp Webhook] Tipo de mensaje ignorado: {}", type);
            return;
        }
        String from = message.path("from").asText();
        String body = message.path("text").path("body").asText();
        if (from.isBlank() || body.isBlank()) {
            return;
        }
        log.info("[WhatsApp Webhook] Mensaje recibido de +{}: {}", from, body);
        orderUseCase.processWhatsAppOrder(body, "+" + from);
    }

    private static final class MessageDigestSafeEquals {
        private MessageDigestSafeEquals() {
        }

        private static boolean equals(String expected, String actual) {
            byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
            byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);
            if (expectedBytes.length != actualBytes.length) {
                return false;
            }
            int result = 0;
            for (int i = 0; i < expectedBytes.length; i++) {
                result |= expectedBytes[i] ^ actualBytes[i];
            }
            return result == 0;
        }
    }
}
