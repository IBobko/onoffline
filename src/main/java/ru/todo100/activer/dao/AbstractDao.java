package ru.todo100.activer.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.todo100.activer.model.DateChanges;
import ru.todo100.activer.model.Item;
import ru.todo100.activer.model.WallItem;
import ru.todo100.activer.qualifier.WallQualifier;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressWarnings({"unchecked"})
abstract public class AbstractDao<T> {
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    abstract public Class getItemClass();

    public Criteria getCriteria() {
        final Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }
        return session.createCriteria(getItemClass());
    }

    public Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    @Transactional
    public void delete(Integer id) {
        final Object object = getSession().load(getItemClass(), id);
        if (object != null) {
            getSession().delete(object);
            getSession().flush();
        }
    }

    @Transactional
    public Object get(Integer id) {
        return getSession().get(getItemClass(), id);
    }

    @Transactional
    public void save(Item item) {
        final Session session = getSession();
        if (item.getId() != null) {
            session.merge(item);
        } else {
            /*todo необходимо по возможности использовать, где это возможно этот метод*/
            if (item instanceof DateChanges) {
                ((DateChanges) item).setCreatedDate(new GregorianCalendar());
            }
            session.persist(item);
        }
        session.flush();
        session.clear();
    }

}
