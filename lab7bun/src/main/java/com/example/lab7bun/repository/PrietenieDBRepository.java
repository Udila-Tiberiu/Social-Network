package com.example.lab7bun.repository;

import com.example.lab7bun.domain.Mesaj;
import com.example.lab7bun.domain.Utilizator;
import com.example.lab7bun.domain.validators.PrietenieValidator;
import com.example.lab7bun.domain.Prietenie;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PrietenieDBRepository implements Repository<Long, Prietenie>{
    private String url;
    private String username;
    private String password;
    private PrietenieValidator prietenieValidator;
    private UtilizatorDBRepository utilizatorDBRepository;

    public PrietenieDBRepository(String url, String username, String password, PrietenieValidator prietenieValidator, UtilizatorDBRepository utilizatorDBRepository){
        this.url=url;
        this.username=username;
        this.password=password;
        this.prietenieValidator=prietenieValidator;
        this.utilizatorDBRepository=utilizatorDBRepository;
    }

    @Override
    public Optional<Prietenie> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from prietenie " +
                    "where id = ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_ = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("din");
                LocalDateTime din = timestamp.toLocalDateTime();

                Optional<Utilizator> u1 = utilizatorDBRepository.findOne(id1);
                Optional<Utilizator> u2 = utilizatorDBRepository.findOne(id2);
                Prietenie prietenie = new Prietenie(u1, u2, din);
                prietenie.setId(id_);
                return Optional.ofNullable(prietenie);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from prietenie");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id_ = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("din");
                LocalDateTime din = timestamp.toLocalDateTime();
                Optional<Utilizator> u1 = utilizatorDBRepository.findOne(id1);
                Optional<Utilizator> u2 = utilizatorDBRepository.findOne(id2);
                if (u1.isPresent() && u2.isPresent()) {
                    Prietenie prietenie = new Prietenie(u1, u2, din);
                    prietenie.setId(id_);
                    prietenii.add(prietenie);
                }

            }
            return prietenii;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        prietenieValidator.validate(entity);
        String insertSQL="insert into prietenie (id,din,id1,id2) values(?,?,?,?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement=connection.prepareStatement(insertSQL);)
        {
            statement.setLong(1,entity.getId());
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //statement.setString(2, entity.getData().toString());
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(entity.getData());
            statement.setTimestamp(2,timestamp);
            statement.setLong(3,entity.getUtilizator1().getId());
            statement.setLong(4,entity.getUtilizator2().getId());
            int response=statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        String deleteSQL="delete from prietenie where id=?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL);
        ) {
            statement.setLong(1, id);

            Optional<Prietenie> prietenie = findOne(id);

            int response = 0;
            if (prietenie.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : prietenie;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        prietenieValidator.validate(entity);
        if(entity==null)
        {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL="update prietenie set id1=?,id2=? where id=?";
        try(var connection= DriverManager.getConnection(url, username, password);
            PreparedStatement statement=connection.prepareStatement(updateSQL);)
        {
            statement.setLong(1,entity.getUtilizator1().getId());
            statement.setLong(2,entity.getUtilizator2().getId());
            statement.setLong(3,entity.getId());

            int response= statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void updateAll(List<Prietenie> entities) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false); // Start a transaction

            try (PreparedStatement statement = connection.prepareStatement("UPDATE prietenie SET id1 = ?, id2 = ? WHERE id = ?")) {
                for (Prietenie entity : entities) {
                    statement.setString(1, entity.getUtilizator1().getId().toString());
                    statement.setString(2, entity.getUtilizator2().getId().toString());
                    statement.setString(3, entity.toString());
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit(); // Commit the transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback the transaction if an error occurs
                System.out.println("Error while updating friendships: " + e);
            }
        } catch (SQLException e) {
            System.out.println("Error while establishing database connection: " + e);
        }
    }
}
