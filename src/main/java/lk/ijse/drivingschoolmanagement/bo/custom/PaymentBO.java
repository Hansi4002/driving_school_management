package lk.ijse.drivingschoolmanagement.bo.custom;

import lk.ijse.drivingschoolmanagement.dto.PaymentDTO;

import java.sql.SQLException;
import java.util.List;

public interface PaymentBO {
    boolean savePayment(PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException;
    boolean updatePayment(PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException;
    boolean deletePayment(String paymentId) throws SQLException, ClassNotFoundException;
    PaymentDTO findPaymentById(String paymentId) throws SQLException, ClassNotFoundException;
    List<PaymentDTO> findAllPayments() throws SQLException, ClassNotFoundException;
    String getNextPaymentId() throws SQLException, ClassNotFoundException;

    double getMonthlyRevenue();
}