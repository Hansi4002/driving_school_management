package lk.ijse.drivingschoolmanagement.bo.impl;

import lk.ijse.drivingschoolmanagement.bo.custom.InstructorBO;
import lk.ijse.drivingschoolmanagement.dao.DAOFactory;
import lk.ijse.drivingschoolmanagement.dao.custom.InstructorDAO;
import lk.ijse.drivingschoolmanagement.dto.InstructorDTO;
import lk.ijse.drivingschoolmanagement.entity.Instructor;
import lk.ijse.drivingschoolmanagement.util.HibernateUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstructorBOImpl implements InstructorBO {

    private final InstructorDAO instructorDAO = (InstructorDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOType.INSTRUCTOR);

    @Override
    public boolean saveInstructor(InstructorDTO instructorDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Instructor instructor = convertToEntity(instructorDTO);
                return instructorDAO.save(instructor, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to save instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateInstructor(InstructorDTO instructorDTO) throws SQLException {
        try {
            return HibernateUtil.execute(session -> {
                Optional<Instructor> optionalInstructor = instructorDAO.findById(instructorDTO.getInstructorId(), session);
                if (optionalInstructor.isEmpty()) {
                    throw new RuntimeException("Instructor not found for update: " + instructorDTO.getInstructorId());
                }

                Instructor instructor = optionalInstructor.get();
                updateInstructorEntity(instructor, instructorDTO);
                return instructorDAO.update(instructor, session);
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to update instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteInstructor(String instructorId) throws SQLException {
        try {
            return HibernateUtil.execute(session ->
                    instructorDAO.deleteById(instructorId, session)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to delete instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public InstructorDTO findInstructorById(String instructorId) throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    instructorDAO.findById(instructorId, session)
                            .map(InstructorDTO::new)
                            .orElse(null)
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find instructor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<InstructorDTO> findAllInstructors() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    instructorDAO.findAll(session).stream()
                            .map(InstructorDTO::new)
                            .collect(Collectors.toList())
            );
        } catch (RuntimeException e) {
            throw new SQLException("Failed to find all instructors: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNextInstructorId() throws SQLException {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                Optional<String> lastIdOpt = instructorDAO.getLastId(session);
                if (lastIdOpt.isPresent()) {
                    String lastId = lastIdOpt.get();
                    int num = Integer.parseInt(lastId.replaceAll("\\D", "")) + 1;
                    return String.format("I%03d", num);
                }
                return "I001";
            });
        } catch (RuntimeException e) {
            throw new SQLException("Failed to generate next instructor ID: " + e.getMessage(), e);
        }
    }

    @Override
    public int getTotalInstructors() {
        try {
            return HibernateUtil.executeWithoutTransaction(session -> {
                String hql = "SELECT COUNT(i.instructorId) FROM Instructor i";
                Long count = (Long) session.createQuery(hql).uniqueResult();
                return count != null ? count.intValue() : 0;
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Instructor convertToEntity(InstructorDTO instructorDTO) {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(instructorDTO.getInstructorId());
        instructor.setName(instructorDTO.getName());
        instructor.setEmail(instructorDTO.getEmail());
        instructor.setPhoneNo(instructorDTO.getPhoneNo());
        instructor.setAvailable(instructorDTO.isAvailable());
        return instructor;
    }

    private void updateInstructorEntity(Instructor instructor, InstructorDTO instructorDTO) {
        instructor.setName(instructorDTO.getName());
        instructor.setEmail(instructorDTO.getEmail());
        instructor.setPhoneNo(instructorDTO.getPhoneNo());
        instructor.setAvailable(instructorDTO.isAvailable());
    }
}