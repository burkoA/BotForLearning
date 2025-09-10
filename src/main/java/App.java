import model.Dictionary;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utility.HibernateUtil;

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
    }
}
