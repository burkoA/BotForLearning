package functionality;

import model.BotDictionary;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utility.TokenEncryption;

import java.util.*;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient = new OkHttpTelegramClient(new String(Base64.getDecoder().decode(TokenEncryption.BOT_TOKEN)));
    private final BotCommands botCommands = new BotCommands();
    private static final int NUMBER_OF_QUESTIONS = 12;

    private final Map<String, Integer> userQuestionIndex = new HashMap<>();
    private final Map<String, Integer> userScore = new HashMap<>();
    private final Map<String, List<BotDictionary>> userQuestions = new HashMap<>();


    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            try {
                if(text.startsWith("/quizua")) {
                    if (userQuestions.containsKey(chatId)) {
                        telegramClient.execute(new SendMessage(chatId, "Your previous quiz was canceled. Starting a new one."));
                        userQuestions.remove(chatId);
                    }
                    int numQuestions = NUMBER_OF_QUESTIONS;
                    String[] parts = text.split(" ");
                    if(parts.length > 1) {
                        try {
                            numQuestions = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            telegramClient.execute(new SendMessage(chatId, "Second argument must be a number!"));
                        };
                    }
                    startQuiz(chatId, numQuestions);
                } else if(text.equals("/help")) {
                    telegramClient.execute(new SendMessage(chatId,botCommands.helpCommand()));
                } else if(userQuestions.containsKey(chatId)){
                    System.out.println(text);
                    handleAnswer(chatId,text);
                } else {
                    telegramClient.execute(new SendMessage(chatId,"Wrong command! :("));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startQuiz(String chatId, int numQuestions) throws TelegramApiException {
        List<BotDictionary> allWords = botCommands.getAllWords();
        Collections.shuffle(allWords);

        List<BotDictionary> quizWords = allWords.subList(0, Math.min(numQuestions, allWords.size()));

        userQuestions.put(chatId, quizWords);
        userQuestionIndex.put(chatId,0);
        userScore.put(chatId,0);

        sendQuestion(chatId);
    }

    private void sendQuestion(String chatId) throws TelegramApiException {
        int index = userQuestionIndex.get(chatId);
        List<BotDictionary> quizWords = userQuestions.get(chatId);

        if(index >= quizWords.size()) {
            int score = userScore.get(chatId);
            telegramClient.execute(new SendMessage(chatId, "Quiz finished! Your score: " + score + "/" + quizWords.size()));
            userQuestions.remove(chatId);
            userQuestionIndex.remove(chatId);
            userScore.remove(chatId);
            return;
        }

        BotDictionary question = quizWords.get(index);

        String[] incorrect = botCommands.getRandomPolishWords(userQuestions
                .get(chatId)
                .get(userQuestionIndex.get(chatId))
                .getPolishWord());

        telegramClient.execute(new SendMessage(chatId, "How you gonna translate it - " + question.getUkrainianWord() + " ?"));
        System.out.println(question.getUkrainianWord());
        telegramClient.execute(botCommands.sendCustomKeyboard(chatId,incorrect, question.getPolishWord()));
    }

    private void handleAnswer(String chatId, String answer) throws TelegramApiException {
        int index = userQuestionIndex.get(chatId);
        List<BotDictionary> quizWords = userQuestions.get(chatId);
        BotDictionary current = quizWords.get(index);

        if (answer.equals(current.getPolishWord())) {
            telegramClient.execute(new SendMessage(chatId, "Correct!"));
            userScore.put(chatId, userScore.get(chatId) + 1);
        } else {
            telegramClient.execute(new SendMessage(chatId, "Wrong! Correct answer was: " + current.getPolishWord()));
        }

        // Move to next question
        userQuestionIndex.put(chatId, index + 1);
        sendQuestion(chatId);
    }
}
