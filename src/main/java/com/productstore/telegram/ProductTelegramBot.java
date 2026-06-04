package com.productstore.telegram;

import com.productstore.dto.CreateProductWithMetadataRequest;
import com.productstore.dto.ProductResponse;
import com.productstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ProductTelegramBot extends TelegramLongPollingBot {
    private final ProductService productService;
    private final Map<Long, String> userState = new HashMap<>();
    private final Map<Long, CreateProductWithMetadataRequest> userData = new HashMap<>();

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String username;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.equals("/start")) {
                sendMessage(chatId, "Привет! Я бот магазина 🚀");
                return;
            }

            if (text.equals("/products")) {
                handleProducts(chatId);
                return;
            }
            if (text.startsWith("/search")) {
                handleSearch(chatId, text);
                return;
            }
            if (text.startsWith("/product")) {
                handleProduct(chatId, text);
                return;
            }
            if (text.equals("/create")) {

                userState.put(chatId, "WAIT_NAME");

                userData.put(chatId, new CreateProductWithMetadataRequest());

                sendMessage(chatId, "Введи название продукта");

                return;
            }
            String state = userState.get(chatId);

            if (state == null) return;

            if (state.equals("WAIT_NAME")) {

                userData.get(chatId).setName(text);
                userState.put(chatId, "WAIT_FIRM");

                sendMessage(chatId, "Введи фирму:");
                return;
            }


            if (state.equals("WAIT_FIRM")) {

                userData.get(chatId).setFirm(text);
                userState.put(chatId, "WAIT_DESCRIPTION");

                sendMessage(chatId, "Введи описание:");
                return;
            }
            if (state.equals("WAIT_DESCRIPTION")) {

                userData.get(chatId).setDescription(text);

                CreateProductWithMetadataRequest request = userData.get(chatId);

                productService.createProductWithMetadata(request);

                sendMessage(chatId, "Продукт создан ✅");

                userState.remove(chatId);
                userData.remove(chatId);
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleProducts(Long chatId) {

        List<ProductResponse> products = productService.getAllProducts();

        if (products.isEmpty()) {
            sendMessage(chatId, "Нет продуктов");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (ProductResponse p : products) {
            sb.append(p.getId())
                    .append(". ")
                    .append(p.getName())
                    .append("\n");
        }

        sendMessage(chatId, sb.toString());
    }

    private void handleSearch(Long chatId, String text) {
        String name = text.replace("/search ", "");

        List<ProductResponse> products = productService.getProductsByName(name);

        if (products.isEmpty()) {
            sendMessage(chatId, "Ничего не найдено");
            return;
        }

        StringBuilder sb = new StringBuilder("Результаты:\n");

        for (ProductResponse p : products) {
            sb.append(p.getName())
                    .append(" (")
                    .append(p.getFirm())
                    .append(")\n");
        }

        sendMessage(chatId, sb.toString());
    }

    private void handleProduct(Long chatId, String text) {

        try {

            String[] parts = text.split(" ");

            if (parts.length < 2) {
                sendMessage(chatId, "Напиши команду так: /product 1");
                return;
            }

            Long id = Long.parseLong(parts[1]);

            ProductResponse product = productService.getProductById(id);

            String response = """
                ID: %d
                Название: %s
                Фирма: %s
                Описание: %s
                """.formatted(
                    product.getId(),
                    product.getName(),
                    product.getFirm(),
                    product.getDescription()
            );

            sendMessage(chatId, response);

        } catch (Exception e) {
            sendMessage(chatId, "Продукт не найден");
        }
    }
}