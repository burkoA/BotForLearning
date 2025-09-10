package functionality;

import model.BotDictionary;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utility.HibernateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DictionaryData {
    public List<BotDictionary> getWords() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BotDictionary> query = session.createQuery("FROM BotDictionary", BotDictionary.class);
            return query.getResultList();
        }
    }

    public List<String> getPolishWords() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BotDictionary> query = session.createQuery("FROM BotDictionary", BotDictionary.class);
            return query.stream().map(BotDictionary::getPolishWord).collect(Collectors.toList());
        }
    }

    public List<String> getUkrainianWords() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BotDictionary> query = session.createQuery("FROM BotDictionary", BotDictionary.class);
            return query.stream().map(BotDictionary::getUkrainianWord).collect(Collectors.toList());
        }
    }
}
