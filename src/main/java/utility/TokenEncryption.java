package utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class TokenEncryption {
    public static final String BOT_TOKEN = encodeBase();

    private static String encodeBase() {
        try {
            return Base64.getEncoder().encodeToString(Files.readString(Path.of("secret.txt")).getBytes());
        } catch (IOException e) {
            System.out.println("Can't read token!");
        }

        return "";
    }
}
