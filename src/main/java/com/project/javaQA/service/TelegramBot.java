package com.project.javaQA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.project.javaQA.config.BotConfig;
import com.project.javaQA.model.Questions;
import com.project.javaQA.model.QuestionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

  final   BotConfig config;
  static final String HELP_TEXT = "JavaQ&A - бот, который генерирует вопросы для интервью на позицию Java Developer.\n" +
          "Для работы с ботом достаточно выбрать интересующую тему(раздел)\n" +
          "JavaQ&A выведит на экран наиболее популярные вопросы с ответами.";
 @Autowired
  private QuestionsRepository questionsRepository;
    public TelegramBot(BotConfig config) {

        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "приветствие"));
        listOfCommands.add(new BotCommand("/section", "выбор раздела"));
        listOfCommands.add(new BotCommand("/help", "информация о JavaQ&A"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }


    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":

                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        TypeFactory typeFactory = objectMapper.getTypeFactory();
                        List<Questions> questionsList = objectMapper.readValue(new File("db/questions.json"),
                                typeFactory.constructCollectionType(List.class, Questions.class));
                        questionsRepository.saveAll(questionsList);
                    }
                    catch (Exception e) {
                        log.error(Arrays.toString(e.getStackTrace()));
                    }
                        break;

               case "/help":
                       sendMessage(chatId, HELP_TEXT);
                       break;
                default:

                        sendMessage(chatId, "Извините! Данная команда не поддерживается!");


            }
        }

    }
    private void startCommandReceived(long chatId, String name) {

        String answer = "Здраствуйте,  " + name + "! Выберите раздел! ";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
