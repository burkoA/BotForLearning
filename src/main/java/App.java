import functionality.Bot;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utility.TokenEncryption;

import java.util.Base64;

public class App {
    public static void main(String[] args) {
        try {
            String botToken = new String(Base64.getDecoder().decode(TokenEncryption.BOT_TOKEN));
            TelegramBotsLongPollingApplication t = new TelegramBotsLongPollingApplication();
            t.registerBot(botToken, new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
