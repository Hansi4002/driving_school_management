package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.PaymentBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.PaymentDAO;
import lk.ijse.drivingschoolmanagement.dao.custom.StudentDAO;
import lk.ijse.drivingschoolmanagement.dto.PaymentDTO;
import lk.ijse.drivingschoolmanagement.entity.Payment;
import lk.ijse.drivingschoolmanagement.entity.Student;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaymentBOImpl implements PaymentBO {

    private final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PAYMENT);
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.STUDENT);

    @Override
    public boolean savePayment(PaymentDTO paymentDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Student> studentOpt = studentDAO.findById(paymentDTO.getStudentId(), session);
                if (studentOpt.isEmpty()) {
                    throw new RuntimeException("Student not found: " + paymentDTO.getStudentId());
                }

                Payment payment = convertToEntity(paymentDTO);
                payment.setStudent(studentOpt.get());

                return paymentDAO.save(payment, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save payment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePayment(PaymentDTO paymentDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Payment> existingOpt = paymentDAO.findById(paymentDTO.getPaymentId(), session);
                if (existingOpt.isEmpty()) {
                    throw new RuntimeException("Payment not found for update: " + paymentDTO.getPaymentId());
                }

                Optional<Student> studentOpt = studentDAO.findById(paymentDTO.getStudentId(), session);
                if (studentOpt.isEmpty()) {
                    throw new RuntimeException("Student not found: " + paymentDTO.getStudentId());
                }

                Payment payment = existingOpt.get();
                updatePaymentEntity(payment, paymentDTO);
                payment.setStudent(studentOpt.get());

                return paymentDAO.update(payment, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update payment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePayment(String paymentId) throws SQLException {
        try {
            return HibernateUtil.execute(session ->
                    paymentDAO.deleteById(paymentId, session)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentDTO findPaymentById(String paymentId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    paymentDAO.findById(paymentId, session)
                            .map(PaymentDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find payment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentDTO> findAllPayments() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    paymentDAO.findAll(session).stream()
                            .map(PaymentDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all payments: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextPaymentId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastIdOpt = paymentDAO.getLastId(session);
                if (lastIdOpt.isPresent()) {
                    String lastId = lastIdOpt.get();
                    int num = Integer.parseInt(lastId.replaceAll("\\D", "")) + 1;
                    return String.format("P%03d", num);
                }
                return "P001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next payment ID: " + e.getMessage(), e);
        }
    }

    @Override
    public double getMonthlyRevenue() {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                LocalDate now = LocalDate.now();
                String hql = "SELECT SUM(p.amount) FROM Payment p " +
                        "WHERE MONTH(p.date) = :currentMonth AND YEAR(p.date) = :currentYear";
                Double revenue = (Double) session.createQuery(hql)
                        .setParameter("currentMonth", now.getMonthValue())
                        .setParameter("currentYear", now.getYear())
                        .uniqueResult();
                return revenue != null ? revenue : 0.0;
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private Payment convertToEntity(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentDTO.getPaymentId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setDate(paymentDTO.getDate());
        payment.setStatus(paymentDTO.getStatus());
        return payment;
    }

    private void updatePaymentEntity(Payment payment, PaymentDTO paymentDTO) {
        payment.setAmount(paymentDTO.getAmount());
        payment.setDate(paymentDTO.getDate());
        payment.setStatus(paymentDTO.getStatus());
    }
}