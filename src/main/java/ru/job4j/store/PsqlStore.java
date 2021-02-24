package ru.job4j.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import ru.job4j.models.*;
import org.hibernate.cfg.Configuration;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class PsqlStore implements Store {

    private final SessionFactory sf;

    private PsqlStore() {
        this.sf = new Configuration()
                .configure("hibernatecarsale.cfg.xml")
                .buildSessionFactory();
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Integer createAdvertisement(Advertisement advertisement) {
        return tx(session -> {
            session.save(advertisement);
            return advertisement.getId();
        });
    }

    @Override
    public void updateAdvertisement(Advertisement advertisement) {
        tx(session -> {
            session.update(advertisement);
            return advertisement.getId();
        });
    }

    @Override
    public List<Status> findAllStatus() {
        return tx(session -> session.createQuery("from Status").list());
    }

    @Override
    public List<Advertisement> findAllAdvertisements() {
        return tx(session -> session.createQuery("from Advertisement").list());
    }

    @Override
    public List<Brand> findAllBrands() {
        return tx(session -> session.createQuery("from Brand").list());
    }

    @Override
    public List<Model> findModelByBrandId(Integer brandId) {
        return tx(session -> {
            Query<Model> query = session.createQuery("select m from Model m inner join Brand b "
                    + "on m.brand.id = b.id where b.id = :brandId");
            query.setParameter("brandId", brandId);
            return query.list();
        });
    }

    @Override
    public Advertisement findAdvertisementById(Integer advertisementId) {
        return tx(session -> session.get(Advertisement.class, advertisementId));
    }

    @Override
    public Integer createUser(User user) {
        return tx(session -> {
            try {
                session.save(user);
                return user.getId();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    public User findUserByLogin(String login) {
        return (User) this.tx(
                session -> session
                        .createQuery("FROM User WHERE login = :email")
                        .setParameter("email", login)
                        .uniqueResult()
        );
    }

    @Override
    public Set<Advertisement> findAdvertisementsByParams(Integer brandId, boolean withPhoto, boolean lastDate) {
        return tx(session -> {
            DetachedCriteria criteria = DetachedCriteria.forClass(Advertisement.class);
            if (withPhoto) {
                criteria.add(Restrictions.isNotNull("photo"));
            }
            if (lastDate) {
                String format = "yyyy-MM-dd";
                String field = "createdDate";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Calendar c = Calendar.getInstance();
                try {
                    String date = sdf.format(c.getTime());
                    Date fromDate = new SimpleDateFormat(format).parse(date);
                    c.add(Calendar.DATE, 1);
                    String date1 = sdf.format(c.getTime());
                    Date toDate = new SimpleDateFormat(format).parse(date1);
                    criteria.add(Restrictions.ge(field, fromDate));
                    criteria.add(Restrictions.le(field, toDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (brandId != null) {
                criteria.createCriteria("model").createCriteria("brand").add(Restrictions.eq("id", brandId));
            }
            List<Advertisement> list = criteria.getExecutableCriteria(session).list();
            return (Set<Advertisement>) new HashSet<>(list);
        });
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
