package com.example.lab7bun.repository;

import com.example.lab7bun.domain.Cerere;
import com.example.lab7bun.domain.Status;
import com.example.lab7bun.domain.Utilizator;
import com.example.lab7bun.domain.validators.CerereValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CerereDBRepository implements Repository<Long,Cerere>{
        private String url;
        private String username;
        private String password;
        private CerereValidator cerereValidator;
        private UtilizatorDBRepository utilizatorDBRepository;

        public CerereDBRepository(String url, String username, String password, CerereValidator cerereValidator, UtilizatorDBRepository utilizatorDBRepository){
            this.url=url;
            this.username=username;
            this.password=password;
            this.cerereValidator=cerereValidator;
            this.utilizatorDBRepository=utilizatorDBRepository;
        }

        @Override
        public Optional<Cerere> findOne(Long id) {
            try(Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from cerere " +
                        "where id = ?");

            ) {
                statement.setLong(1, id);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()) {
                    Long id_ = resultSet.getLong("id");
                    Long id1 = resultSet.getLong("id1");
                    Long id2 = resultSet.getLong("id2");
                    Status status= Status.valueOf(resultSet.getString("status"));

                    Optional<Utilizator> u1 = utilizatorDBRepository.findOne(id1);
                    Optional<Utilizator> u2 = utilizatorDBRepository.findOne(id2);
                    Cerere prietenie = new Cerere(u1, u2, status);
                    prietenie.setId(id_);
                    return Optional.ofNullable(prietenie);
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Iterable<Cerere> findAll() {
            Set<Cerere> cereri = new HashSet<>();

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("select * from cerere");
                 ResultSet resultSet = statement.executeQuery()
            ) {

                while (resultSet.next())
                {
                    Long id_ = resultSet.getLong("id");
                    Long id1 = resultSet.getLong("id1");
                    Long id2 = resultSet.getLong("id2");
                    Status status= Status.valueOf(resultSet.getString("status"));

                    Optional<Utilizator> u1 = utilizatorDBRepository.findOne(id1);
                    Optional<Utilizator> u2 = utilizatorDBRepository.findOne(id2);

                    if (u1.isPresent() && u2.isPresent()) {
                        Cerere cerere = new Cerere(u1, u2, status);
                        cerere.setId(id_);
                        cereri.add(cerere);
                    }

                }
                return cereri;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<Cerere> save(Cerere entity) {
            cerereValidator.validate(entity);
            String insertSQL="insert into cerere (id,id1,id2,status) values(?,?,?,?)";
            try (var connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement=connection.prepareStatement(insertSQL);)
            {
                statement.setLong(1,entity.getId());
                statement.setLong(2,entity.getUtilizator1().getId());
                statement.setLong(3,entity.getUtilizator2().getId());
                statement.setString(4,entity.getStatus().toString());
                int response=statement.executeUpdate();
                return response==0 ? Optional.of(entity) : Optional.empty();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<Cerere> delete(Long id) {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }

            String deleteSQL="delete from cerere where id=?";
            try (var connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(deleteSQL);
            ) {
                statement.setLong(1, id);

                Optional<Cerere> cerere = findOne(id);

                int response = 0;
                if (cerere.isPresent()) {
                    response = statement.executeUpdate();
                }

                return response == 0 ? Optional.empty() : cerere;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<Cerere> update(Cerere entity) {
            cerereValidator.validate(entity);
            if(entity==null)
            {
                throw new IllegalArgumentException("Entity cannot be null!");
            }
            String updateSQL="update cerere set id1=?,id2=?,status=? where id=?";
            try(var connection= DriverManager.getConnection(url, username, password);
                PreparedStatement statement=connection.prepareStatement(updateSQL);)
            {
                statement.setLong(1,entity.getUtilizator1().getId());
                statement.setLong(2,entity.getUtilizator2().getId());
                statement.setString(3,entity.getStatus().toString());
                statement.setLong(4,entity.getId());

                int response= statement.executeUpdate();
                return response==0 ? Optional.of(entity) : Optional.empty();
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
}