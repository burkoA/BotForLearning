import functionality.Bot;
import model.Dictionary;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import utility.HibernateUtil;
import utility.TokenEncryption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class App {
    public static void main(String[] args) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Dictionary> query = session.createQuery("FROM model.Dictionary", Dictionary.class);
//            List<Dictionary> words = query.getResultList();
//
//            for (Dictionary word : words) {
//                System.out.println(word.getPolishWord() + " -> " + word.getUkrainianWord());
//            }
//        }

        try {
            String botToken = new String(Base64.getDecoder().decode(TokenEncryption.BOT_TOKEN));
            TelegramBotsLongPollingApplication t = new TelegramBotsLongPollingApplication();
            t.registerBot(botToken, new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
