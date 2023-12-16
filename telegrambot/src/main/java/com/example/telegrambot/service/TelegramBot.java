package com.example.telegrambot.service;

import com.example.telegrambot.category.ElectronicItem;
import com.example.telegrambot.category.ElectronicItemRepository;
import com.example.telegrambot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private ElectronicItemService electronicItemService;

    static final String HELP_TEXT = "Available commands:\n\n" + "/viewtree - displaying the category tree\n\n" +
            "/addelement - add new element\n\n" + "/removeelement - delete element\n";


    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/viewtree", "displaying the category tree"));
        listofCommands.add(new BotCommand("/addelement", "add new element"));
        listofCommands.add(new BotCommand("/removeelement", "delete element"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }

    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String[] tokens = messageText.split("\\s+");



            switch (tokens[0]) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/addelement":
                    addElementCommandReceived(chatId, tokens);
                    break;
                case "/removeelement":
                    removeElementCommandReceived(chatId, tokens);
                    break;
                case "/viewtree":
                    viewCatalogCommandReceived(chatId);
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Извините, такой команды не существует");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Здравствуйте, " + name + ", добро пожаловать в магазин электроники Казахстан!";
        log.info("Replied to user: " + name);
        sendMessage(chatId, answer);
    }

    private void addElementCommandReceived(long chatId, String[] tokens) {
        if (tokens.length == 2) {
            String itemName = tokens[1];
            electronicItemService.addElectronicItem(itemName);
            sendMessage(chatId, "Electronic item '" + itemName + "' added.");
        } else {
            sendMessage(chatId, "Invalid syntax. Usage: /addelement <item>");
        }
    }


    private void viewCatalogCommandReceived(long chatId) {
        List<ElectronicItem> electronicItems = electronicItemService.getAllElectronicItems();
        StringBuilder result = new StringBuilder("Electronic Catalog:\n");
        for (ElectronicItem electronicItem : electronicItems) {
            result.append(electronicItem.getName()).append("\n");
        }
        sendMessage(chatId, result.toString());
    }

    private void removeElementCommandReceived(long chatId, String[] tokens) {
        if (tokens.length == 2) {
            String itemName = tokens[1];
            if (electronicItemService.removeElectronicItem(itemName)) {
                sendMessage(chatId, "Electronic item '" + itemName + "' removed.");
            } else {
                sendMessage(chatId, "Electronic item '" + itemName + "' not found.");
            }
        } else {
            sendMessage(chatId, "Invalid syntax. Usage: /removeelement <item>");
        }
    }



    @Override
    public String getBotUsername() {
        return config.getBotName();
    }


    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
