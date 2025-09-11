package functionality;

import model.BotDictionary;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
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
        Set<String> options = new HashSet<>();
        List<String> polishWords = data.getPolishWords();
        int listSize = polishWords.size();

        while(options.size() < 3){
            String word = polishWords.get(random.nextInt(listSize));
            if(!word.equals(correctWord)){
                options.add(word);
            }
        }

        return options.toArray(new String[3]);
    }

    public String[] getRandomUkrainianWord(String correctWord) {
        Random random = new Random();
        Set<String> options = new HashSet<>();
        List<String> ukrainianWord = data.getUkrainianWords();
        int listSize = ukrainianWord.size();

        while(options.size() < 3){
            String word = ukrainianWord.get(random.nextInt(listSize));
            if(!word.equals(correctWord)){
                options.add(word);
            }
        }

        return options.toArray(new String[3]);
    }

    public SendMessage sendCustomKeyboard(String chatId, String[] incorrectWords, String correctWord) {
        List<String> totalWords = Arrays.stream(incorrectWords).collect(Collectors.toList());
        totalWords.add(correctWord);

        SendMessage message = new SendMessage(chatId,"Choose the correct answer:");

        Collections.shuffle(totalWords);
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (int i = 0; i < totalWords.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();
            row.add(totalWords.get(i));
            if (i + 1 < totalWords.size()) row.add(totalWords.get(i + 1));
            keyboard.add(row);
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    public String startMessage() {
        return "Hello! I'm bot for helping you learn polish and ukrainian language! \n"
                + "I will create for you small quiz and if you are don't know something I will write correct answer for you!";
    }

    public String helpCommand() {
        return "/quizua <number of questions> - for making quiz from ukrainian to polish (default value 12) \n" +
                "/quizpl <number of questions> - for making quiz from polish to ukrainian (default value 12)";
    }
}
