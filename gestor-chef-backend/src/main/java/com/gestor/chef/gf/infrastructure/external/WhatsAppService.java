package com.gestor.chef.gf.infrastructure.external;

import com.gestor.chef.gf.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WhatsAppService {

    private static final String BASE_URL  = "https://graph.facebook.com/v19.0";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Bogota"));

    @Value("${app.whatsapp.enabled:false}")
    private boolean enabled;

    @Value("${app.whatsapp.token:}")
    private String token;

    @Value("${app.whatsapp.phone-number-id:}")
    private String phoneNumberId;

    private final RestClient restClient = RestClient.create();

    public void sendOrderConfirmation(Order order, String customerPhone) {
        String body = buildConfirmationMessage(order);
        send(customerPhone, body);
    }

    public void sendOrderStatusUpdate(Order order, String customerPhone, String newStatus) {
        String emoji  = statusEmoji(newStatus);
        String label  = statusLabel(newStatus);
        String msg    = String.format(
                "%s *Actualización de tu pedido*\n\n" +
                "Tu pedido %s está ahora *%s*.\n" +
                "Gracias por elegir La Perla del Mar 🐟",
                emoji, shortId(order.getId()), label);
        send(customerPhone, msg);
    }

    private void send(String toPhone, String text) {
        String phone = normalizePhone(toPhone);

        if (!enabled) {
            log.info("[WhatsApp] SIMULACIÓN → Para: {} | Mensaje: {}", phone, text);
            return;
        }

        if (token.isBlank() || phoneNumberId.isBlank()) {
            log.warn("[WhatsApp] Credenciales no configuradas. Establece app.whatsapp.token y app.whatsapp.phone-number-id.");
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "messaging_product", "whatsapp",
                    "to",               phone,
                    "type",             "text",
                    "text",             Map.of("body", text, "preview_url", false)
            );

            ResponseEntity<String> response = restClient.post()
                    .uri(BASE_URL + "/" + phoneNumberId + "/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[WhatsApp] Mensaje enviado a {} ✔", phone);
            } else {
                log.warn("[WhatsApp] Respuesta inesperada {}: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {

            log.error("[WhatsApp] Error enviando mensaje a {}: {}", phone, e.getMessage());
        }
    }

    private String buildConfirmationMessage(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("✅ *¡Pedido recibido!*\n\n");
        sb.append("📋 *Referencia:* ").append(shortId(order.getId())).append("\n");

        if (order.getTableNumber() != null) {
            sb.append("🪑 *Mesa:* ").append(order.getTableNumber()).append("\n");
        }

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            sb.append("\n*Platos:*\n");
            order.getItems().forEach(item ->
                sb.append("  • ").append(item.getDishName())
                  .append(" x").append(item.getQuantity()).append("\n"));
        }

        if (order.getTotalAmount() != null) {
            sb.append("\n💵 *Total estimado:* $")
              .append(String.format("%,.0f", order.getTotalAmount()))
              .append(" COP\n");
        }

        if (order.getCreatedAt() != null) {
            sb.append("🕐 *Hora:* ").append(FMT.format(order.getCreatedAt())).append("\n");
        }

        sb.append("\n⏱ Tiempo estimado de preparación: *20-30 minutos*\n");
        sb.append("\nGracias por tu pedido en *La Perla del Mar* 🐟🌊");
        return sb.toString();
    }

    private String normalizePhone(String phone) {
        if (phone == null) return "";
        String clean = phone.replaceAll("[^0-9+]", "");
        if (clean.startsWith("+")) return clean.substring(1);
        if (clean.startsWith("57")) return clean;
        if (clean.length() == 10) return "57" + clean;
        return clean;
    }

    private String shortId(String id) {
        if (id == null) return "N/A";
        return "#" + id.substring(Math.max(0, id.length() - 6)).toUpperCase();
    }

    private String statusEmoji(String status) {
        return switch (status != null ? status.toUpperCase() : "") {
            case "IN_PROGRESS" -> "👨‍🍳";
            case "COMPLETED"   -> "✅";
            case "CANCELLED"   -> "❌";
            default            -> "ℹ️";
        };
    }

    private String statusLabel(String status) {
        return switch (status != null ? status.toUpperCase() : "") {
            case "PENDING"     -> "Pendiente";
            case "IN_PROGRESS" -> "En preparación";
            case "COMPLETED"   -> "Listo para entregar";
            case "CANCELLED"   -> "Cancelado";
            default            -> status;
        };
    }
}
