package com.example.lab7bun.repository;

import com.example.lab7bun.domain.Mesaj;
import com.example.lab7bun.domain.Utilizator;
import com.example.lab7bun.domain.validators.MesajValidator;
import com.example.lab7bun.domain.validators.PrietenieValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MesajDBRepository implements Repository<Long, Mesaj>{
    private String url;
    private String username;
    private String password;
    private MesajValidator mesajValidator;
    private UtilizatorDBRepository utilizatorDBRepository;

    public MesajDBRepository(String url, String username, String password, MesajValidator mesajValidator, UtilizatorDBRepository utilizatorDBRepository){
        this.url=url;
        this.username=username;
        this.password=password;
        this.mesajValidator=mesajValidator;
        this.utilizatorDBRepository=utilizatorDBRepository;
    }

    @Override
    public Optional<Mesaj> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from mesaj " +
                    "where id = ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_ = resultSet.getLong("id");
                String textmesaj = resultSet.getString("textmesaj");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("din");
                LocalDateTime din = timestamp.toLocalDateTime();
                Long reply = resultSet.getLong("reply");

                Optional<Utilizator> u1 = utilizatorDBRepository.findOne(id1);
                Optional<Utilizator> u2 = utilizatorDBRepository.findOne(id2);

                Mesaj mesaj = new Mesaj(textmesaj,u1,u2,din,reply);
                mesaj.setId(id_);
                return Optional.ofNullable(mesaj);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Mesaj> findAll() {
        Set<Mesaj> mesaje = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from mesaj");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id_ = resultSet.getLong("id");
                String textmesaj = resultSet.getString("textmesaj");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                java.sql.Timestamp timestamp = resultSet.getTimestamp("din");
                LocalDateTime din = timestamp.toLocalDateTime();
                Long reply = resultSet.getLong("reply");

                Optional<Utilizator> u1 = utilizatorDBRepository.findOne(id1);
                Optional<Utilizator> u2 = utilizatorDBRepository.findOne(id2);
                if (u1.isPresent() && u2.isPresent()) {
                    Mesaj mesaj = new Mesaj(textmesaj, u1.get(), u2.get(), din, reply);
                    mesaj.setId(id_);
                    mesaje.add(mesaj);
                }
            }
            return mesaje;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Mesaj> save(Mesaj entity) {
        mesajValidator.validate(entity);
        String insertSQL="insert into mesaj (id, textmesaj, id1, id2, din, reply) values(?, ?, ?, ?, ?, ?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement=connection.prepareStatement(insertSQL);)
        {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getText());
            statement.setLong(3, entity.getFrom().getId());
            statement.setLong(4, entity.getTo().getId());
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(entity.getData());
            statement.setTimestamp(5,timestamp);
            statement.setLong(6, entity.getReply());
            int response=statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Mesaj> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        String deleteSQL="delete from mesaj where id=?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL);
        ) {
            statement.setLong(1, id);

            Optional<Mesaj> mesaj = findOne(id);

            int response = 0;
            if (mesaj.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : mesaj;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Mesaj> update(Mesaj entity) {
        mesajValidator.validate(entity);
        if(entity==null)
        {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL="update mesaj set textmesaj=?,id1=?,id2=?,din=?,reply=? where id=?";
        try(var connection= DriverManager.getConnection(url, username, password);
            PreparedStatement statement=connection.prepareStatement(updateSQL);)
        {
            statement.setString(1, entity.getText());
            statement.setLong(2, entity.getFrom().getId());
            statement.setLong(3, entity.getTo().getId());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getData()));
            statement.setLong(5, entity.getReply());
            statement.setLong(6,entity.getId());

            int response= statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
