import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;


/**
 * Created by Аглиуллины on 12.09.2017.
 */
public class BotService {

    public static String putPostInBot() {

        Parser parser = new Parser();
        Document doc = null;
        try {
            doc = Jsoup.connect(ConfigManager.getURL()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser.getPostImages(0, doc);
        String result = parser.getPostText(0, doc) + "\n" +
                parser.getVideoPostFormIndex(0, doc) + lineFromList(parser, doc);

        System.out.println(result);
        return result;
    }

    public static String lineFromList(Parser parser, Document doc) {
        StringBuffer result = new StringBuffer();

        try {
            for (String s : parser.getPostImages(0, doc)
                    ) {
                result.append(s + "\n");

            }
        } catch (NullPointerException e) {
            return " ";
        }

        return result.toString();
    }

    public static void main(String[] args) {
        putPostInBot();
    }

}
