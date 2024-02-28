package com.example.lab7bun.repository;

import com.example.lab7bun.domain.Utilizator;
import com.example.lab7bun.domain.validators.UtilizatorValidator;
import com.example.lab7bun.repository.paging.Page;
import com.example.lab7bun.repository.paging.Pageable;
import com.example.lab7bun.repository.paging.PagingRepository;

import java.sql.*;
import java.util.*;

public class UtilizatorDBRepository implements PagingRepository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private UtilizatorValidator utilizatorValidator;

    public UtilizatorDBRepository(String url, String username, String password, UtilizatorValidator utilizatorValidator){
        this.url=url;
        this.username=username;
        this.password=password;
        this.utilizatorValidator=utilizatorValidator;
    }

    @Override
    public Optional<Utilizator> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from utilizator " +
                    "where id = ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String nume = resultSet.getString("nume");
                String prenume = resultSet.getString("prenume");
                Utilizator u = new Utilizator(nume,prenume);
                u.setId(id);
                return Optional.ofNullable(u);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from utilizator");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String nume=resultSet.getString("nume");
                String prenume=resultSet.getString("prenume");
                Utilizator user=new Utilizator(nume,prenume);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        utilizatorValidator.validate(entity);
        String insertSQL="insert into utilizator (id,nume,prenume) values(?,?,?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement=connection.prepareStatement(insertSQL);)
        {
            statement.setLong(1,entity.getId());
            statement.setString(2,entity.getNume());
            statement.setString(3,entity.getPrenume());
            int response=statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        String deleteSQL="delete from utilizator where id=?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL);
        ) {
            statement.setLong(1, id);

            Optional<Utilizator> foundUser = findOne(id);

            int response = 0;
            if (foundUser.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        utilizatorValidator.validate(entity);
        if(entity==null)
        {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL="update utilizator set nume=?,prenume=? where id=?";
        try(var connection= DriverManager.getConnection(url, username, password);
            PreparedStatement statement=connection.prepareStatement(updateSQL);)
        {
            statement.setString(1,entity.getNume());
            statement.setString(2,entity.getPrenume());
            statement.setLong(3,entity.getId());

            int response= statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private int returnNumberOfElements(){
        int numberOfElements=0;
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from utilizator");
             ResultSet resultSet = statement.executeQuery();
        ) {
            // pas 3: process result set
            while (resultSet.next()){
                numberOfElements = resultSet.getInt("count");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return no elements
        return numberOfElements;
    }


    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        int numberOfElements = returnNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        System.out.println(offset + " ?>= "+numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);
        // prereq: create empty set
        List<Utilizator> utilizatori = new ArrayList<>();
        // pas 1: conectarea la baza de date
        try (Connection connection = DriverManager.getConnection(url, username, password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select * from utilizator limit ? offset ?");
        ) {
            statement.setInt(2, offset);
            statement.setInt(1,limit);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next()){
                Long id= resultSet.getLong("id");
                String nume=resultSet.getString("nume");
                String prenume=resultSet.getString("prenume");
                Utilizator currentUtilizator = new Utilizator(nume, prenume);
                currentUtilizator.setId(id);
                utilizatori.add(currentUtilizator);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return result list
        return new Page<>(utilizatori, numberOfElements);
    }
}

