package functionality;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utility.TokenEncryption;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient(new String(Base64.getDecoder().decode(TokenEncryption.BOT_TOKEN)));
    private BotCommands botCommands = new BotCommands();

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String chatId = update.getMessage().getChatId().toString();
            try {
                switch (update.getMessage().getText()) {
                    case "/help":
                            telegramClient.execute(new SendMessage(chatId, botCommands.helpCommand()));
                        break;
                    default:
                        telegramClient.execute(new SendMessage(chatId, "Wrong command! Try to write /help :)"));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
