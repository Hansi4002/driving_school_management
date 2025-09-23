package lk.ijse.drivingschoolmanagement.dao.impl;

import lk.ijse.drivingschoolmanagement.dao.custom.PaymentDAO;
import lk.ijse.drivingschoolmanagement.entity.Payment;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public boolean save(Payment entity, Session session) {
        try {
            session.persist(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Payment entity, Session session) {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(String paymentId, Session session) {
        try {
            Payment payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                session.remove(payment);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Payment> findAll(Session session) {
        try {
            return session.createQuery("from Payment", Payment.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<Payment> findById(String paymentId, Session session) {
        try {
            return Optional.ofNullable(session.get(Payment.class, paymentId));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getLastId(Session session) {
        try {
            List<String> list = session.createQuery("SELECT p.paymentId FROM Payment p ORDER BY p.paymentId DESC", String.class)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}