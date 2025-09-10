package functionality;

import model.BotDictionary;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BotCommands {
    private final DictionaryData data = new DictionaryData();

    public BotCommands() {};

    public List<BotDictionary> getAllWords() {
        return data.getWords();
    }

    public BotDictionary getRandomDictionary() {
        Random random = new Random();
        List<BotDictionary> botDictionaries = data.getWords();
        int listSize = botDictionaries.size();

        return botDictionaries.get(random.nextInt(listSize));
    }

    public String[] getRandomPolishWords(String correctWord) {
        Random random = new Random();
        int numberOfOptions = 3;
        String[] finalWords = new String[numberOfOptions];
        List<String> polishWords = data.getPolishWords();
        int listSize = polishWords.size();
        String lastWord = "";

        for(int i = 0; i < finalWords.length;) {
            String randomWord = polishWords.get(random.nextInt(listSize));
            if(!lastWord.equals(randomWord) && !correctWord.equals(randomWord)) {
                finalWords[i] = randomWord;
                lastWord = randomWord;
                i++;
            }
        }

        return finalWords;
    }

    public SendMessage sendCustomKeyboard(String chatId, String[] incorrectWords, String correctWord) {
        Random random = new Random();
        List<String> totalWords = Arrays.stream(incorrectWords).collect(Collectors.toList());
        totalWords.add(correctWord);
        int randomValue = 0;

        SendMessage message = new SendMessage(chatId,"Choose the correct answer:");

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        
        randomValue = random.nextInt(totalWords.size());
        row.add(totalWords.get(randomValue));
        totalWords.remove(randomValue);
        
        randomValue = random.nextInt(totalWords.size());
        row.add(totalWords.get(randomValue));
        totalWords.remove(randomValue);

        keyboard.add(row);
        
        row = new KeyboardRow();

        randomValue = random.nextInt(totalWords.size());
        row.add(totalWords.get(randomValue));
        totalWords.remove(randomValue);

        randomValue = random.nextInt(totalWords.size());
        row.add(totalWords.get(randomValue));

        keyboard.add(row);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    public String helpCommand() {
        return "/quizua <number of questions> - for making quiz from ukrainian to polish (default value 12) \n" +
                "/quizpl <number of questions> - for making quiz from polish to ukrainian (default value 12)";
    }
}
