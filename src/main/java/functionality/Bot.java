package functionality;

import model.BotDictionary;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utility.TokenEncryption;

import java.util.*;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient = new OkHttpTelegramClient(new String(Base64.getDecoder().decode(TokenEncryption.BOT_TOKEN)));
    private final BotCommands botCommands = new BotCommands();
    private static final int NUMBER_OF_QUESTIONS = 12;
    private final List<String> stickersId = List.of("CAACAgIAAxkBAAEPWY1owpx24wQPu9FDxHw1CLb_N3KSrQACrgIAAjZ2IA54Lv6p2p7e4zYE",
            "CAACAgIAAxkBAAEPWYlowpxklUSUkykssC8aaHvakltLxwACuQIAAjZ2IA7tdOsFZ9JbhTYE",
            "CAACAgIAAxkBAAEPWZFowpygw1qDa-vOi9jRy8T45LJnigACsAIAAjZ2IA7iZ3TN1yUsCTYE",
            "CAACAgIAAxkBAAEPWY9owpyU7b4Rvhy2nDNrzIzY5mAXmQACsQIAAjZ2IA7Lb6f2ctrJ_TYE",
            "CAACAgIAAxkBAAEPWYtowpxwm2Lo_7Cip_4NNr28CLReZwACrwIAAjZ2IA5y5gpS9w5GKzYE");

    private final Map<String, Integer> userQuestionIndex = new HashMap<>();
    private final Map<String, Integer> userScore = new HashMap<>();
    private final Map<String, List<BotDictionary>> userQuestions = new HashMap<>();
    private final Map<String, String> quizVersion = new HashMap<>();

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            try {
                if(text.startsWith("/start")) {
                    telegramClient.execute(new SendMessage(chatId, botCommands.startMessage()));
                } else if(text.startsWith("/quizua")) {
                    if (userQuestions.containsKey(chatId)) {
                        telegramClient.execute(new SendMessage(chatId, "Your previous quiz was canceled. Starting a new one."));
                        clearPreviousQuiz(chatId);
                    }
                    int numQuestions = NUMBER_OF_QUESTIONS;
                    quizVersion.put(chatId,"ua");
                    String[] parts = text.split(" ");
                    if(parts.length > 1) {
                        try {
                            numQuestions = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            telegramClient.execute(new SendMessage(chatId, "Second argument must be a number!"));
                        };
                    }
                    startQuiz(chatId, numQuestions);
                } else if(text.startsWith("/quizpl")) {
                    if (userQuestions.containsKey(chatId)) {
                        telegramClient.execute(new SendMessage(chatId, "Your previous quiz was canceled. Starting a new one."));
                        clearPreviousQuiz(chatId);
                    }
                    int numQuestions = NUMBER_OF_QUESTIONS;
                    quizVersion.put(chatId,"pl");
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
                    handleAnswer(chatId,text);
                } else {
                    telegramClient.execute(new SendMessage(chatId,"Wrong command! :("));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void clearPreviousQuiz(String chatId) {
        userQuestions.remove(chatId);
        userScore.remove(chatId);
        userQuestionIndex.remove(chatId);
        quizVersion.remove(chatId);
    }

    private void startQuiz(String chatId, int numQuestions) throws TelegramApiException {
        List<BotDictionary> allWords = botCommands.getAllWords();
        Collections.shuffle(allWords);

        List<BotDictionary> quizWords = allWords.subList(0, Math.min(numQuestions, allWords.size()));

        userQuestions.put(chatId, quizWords);
        userQuestionIndex.put(chatId,0);
        userScore.put(chatId,0);

        sendQuestion(chatId, quizVersion.get(chatId));
    }

    private void sendQuestion(String chatId, String quizOption) throws TelegramApiException {
        int index = userQuestionIndex.get(chatId);
        List<BotDictionary> quizWords = userQuestions.get(chatId);

        if(index >= quizWords.size()) {
            int score = userScore.get(chatId);
            telegramClient.execute(new SendMessage(chatId, "Quiz finished! Your score: " + score + "/" + quizWords.size()));
            int percentage = (int) (((double) score / quizWords.size()) * 100);
            sendSticker(chatId,percentage);
            userQuestions.remove(chatId);
            userQuestionIndex.remove(chatId);
            userScore.remove(chatId);
            quizVersion.remove(chatId);
            return;
        }

        BotDictionary question = quizWords.get(index);
        String[] incorrect;

        if(quizOption.equals("ua")) {
            incorrect = botCommands.getRandomPolishWords(userQuestions
                    .get(chatId)
                    .get(userQuestionIndex.get(chatId))
                    .getPolishWord());
        } else {
            incorrect = botCommands.getRandomUkrainianWord(userQuestions
                    .get(chatId)
                    .get(userQuestionIndex.get(chatId))
                    .getUkrainianWord());
        }

        String translatedWord = quizOption.equals("ua") ? question.getUkrainianWord() : question.getPolishWord();
        String correctWord = quizOption.equals("ua") ? question.getPolishWord() : question.getUkrainianWord();

        telegramClient.execute(new SendMessage(chatId, "How you gonna translate it - " +  translatedWord + " ?"));
        telegramClient.execute(botCommands.sendCustomKeyboard(chatId,incorrect, correctWord));
    }

    private void handleAnswer(String chatId, String answer) throws TelegramApiException {
        int index = userQuestionIndex.get(chatId);
        List<BotDictionary> quizWords = userQuestions.get(chatId);
        BotDictionary current = quizWords.get(index);
        String correctAnswer = quizVersion.get(chatId).equals("ua") ? current.getPolishWord() : current.getUkrainianWord();

        if (answer.equals(correctAnswer)) {
            telegramClient.execute(new SendMessage(chatId, "Correct!"));
            userScore.put(chatId, userScore.get(chatId) + 1);
        } else {
            telegramClient.execute(new SendMessage(chatId, "Wrong! Correct answer was: " + correctAnswer));
        }

        userQuestionIndex.put(chatId, index + 1);
        sendQuestion(chatId,quizVersion.get(chatId));
    }

    public void sendSticker(String chatId, int result) throws TelegramApiException {
        InputFile stickerFile;
        SendSticker sticker;

        if(result >= 80) {
            stickerFile = new InputFile(stickersId.get(0));
            sticker = new SendSticker(chatId, stickerFile);
        } else if (result >= 60) {
            stickerFile = new InputFile(stickersId.get(1));
            sticker = new SendSticker(chatId, stickerFile);
        } else if (result >= 40) {
            stickerFile = new InputFile(stickersId.get(2));
            sticker = new SendSticker(chatId, stickerFile);
        } else if (result >= 20) {
            stickerFile = new InputFile(stickersId.get(3));
            sticker = new SendSticker(chatId, stickerFile);
        } else {
            stickerFile = new InputFile(stickersId.get(4));
            sticker = new SendSticker(chatId, stickerFile);
        }

        telegramClient.execute(sticker);
    }
}
