package com.example.lab7bun;


import com.example.lab7bun.domain.Cerere;
import com.example.lab7bun.domain.Mesaj;
import com.example.lab7bun.domain.Prietenie;
import com.example.lab7bun.domain.Utilizator;
import com.example.lab7bun.domain.validators.ValidationException;
import com.example.lab7bun.repository.paging.Page;
import com.example.lab7bun.repository.paging.Pageable;
import com.example.lab7bun.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.StreamSupport;

public class HelloController implements Initializable {
    @FXML
    private ListView<Prietenie> friendshipList;

    @FXML
    private ListView<Utilizator> userList;

    @FXML
    private ListView<Mesaj> mesajeList;

    @FXML
    private ListView<Cerere> cerereList;

    @FXML
    private TextField nume1;

    @FXML
    private TextField prenume1;

    @FXML
    private TextField nume2;

    @FXML
    private TextField prenume2;


    private TextField nrluna;

    @FXML
    private TextField str;

    @FXML
    private TextField mesaj;

    @FXML
    private TextField reply;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private TextField nrPages;

    @FXML
    private Button pageButton;

    private int currentPage=0;
    private int numberOfRecordsPerPage = 5;

    private int totalNumberOfElements;


    private final ObservableList<Prietenie> friendsObs = FXCollections.observableArrayList();

    private final ObservableList<Utilizator> userObs = FXCollections.observableArrayList();

    private final ObservableList<Mesaj> msjObs = FXCollections.observableArrayList();

    private final ObservableList<Cerere> cerereObs = FXCollections.observableArrayList();

    private Service service;


    public void setService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        friendsObs.addAll(this.getPrietenie());
        userObs.addAll(this.getUtilizator());
        msjObs.addAll(this.getMesaje());
        cerereObs.addAll(this.getCerere());
        friendshipList.setItems(friendsObs);
        userList.setItems(userObs);
        mesajeList.setItems(msjObs);
        cerereList.setItems(cerereObs);
    }

    public void initData(){
        Page<Utilizator> moviesOnCurrentPage = Service.getInstance().getMoviesOnPage(new Pageable(currentPage, numberOfRecordsPerPage));
        totalNumberOfElements = moviesOnCurrentPage.getTotalNumberOfElements();
        List<Utilizator> userList = StreamSupport.stream(moviesOnCurrentPage.getElementsOnPage().spliterator(), false).toList();
        System.out.println(userList);
        userObs.setAll(userList);
        handlePageNavigationChecks();
    }

    private void handlePageNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*numberOfRecordsPerPage >= totalNumberOfElements);
    }
    public void goToNextPage(ActionEvent actionEvent) {
        System.out.println("NEXT PAGE");
        currentPage++;
        initData();
    }

    public void goToPreviousPage(ActionEvent actionEvent) {
        System.out.println("PREVIOUS PAGE");
        currentPage--;
        initData();
    }

    public void schimbaNrPagina(ActionEvent actionEvenet){
        String nr_s = nrPages.getText();
        try{
            int nr = Integer.parseInt(nr_s);
            if(nr > 0){
                numberOfRecordsPerPage = nr;
            }
            else {
                this.displayErrorAlert("Error Occurred", "Eroare trimitere mesaj", "Nr. de utilizatori pe o pagina trebuie sa fie mai mare decat 0");
            }
        }catch(Exception e){
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare trimitere mesaj", e.getMessage());
        }
        this.actualizeaza();
    }

    public List<Prietenie> getPrietenie(){
        Iterable<Prietenie> prieteni=Service.getInstance().getAllPrietenie();
        List<Prietenie> listapr = StreamSupport.stream(prieteni.spliterator(),false).toList();
        return listapr;
    }

    public List<Utilizator> getUtilizator(){
        Page<Utilizator> moviesOnCurrentPage = Service.getInstance().getMoviesOnPage(new Pageable(currentPage, numberOfRecordsPerPage));
        totalNumberOfElements = moviesOnCurrentPage.getTotalNumberOfElements();
        List<Utilizator> userList = StreamSupport.stream(moviesOnCurrentPage.getElementsOnPage().spliterator(), false).toList();
        handlePageNavigationChecks();
        return userList;
    }

    public List<Mesaj> getMesaje(){
        Iterable<Mesaj> mesaje=Service.getInstance().getAllMesaje();
        List<Mesaj> listamsj = StreamSupport.stream(mesaje.spliterator(),false).toList();
        return listamsj;
    }

    public List<Cerere> getCerere(){
        Iterable<Cerere> cereri=Service.getInstance().getAllCereri();
        List<Cerere> listacereri = StreamSupport.stream(cereri.spliterator(),false).toList();
        return listacereri;
    }

    public void actualizeaza(){
        friendsObs.clear();
        userObs.clear();
        cerereObs.clear();
        friendsObs.setAll(this.getPrietenie());
        userObs.setAll(this.getUtilizator());
        cerereObs.setAll(this.getCerere());
        userList.setItems(userObs);
        friendshipList.setItems(friendsObs);
        cerereList.setItems(cerereObs);
    }

    public void actualizeazaMsj(){
        msjObs.clear();
        msjObs.setAll(this.getMesaje());
        mesajeList.setItems(msjObs);
    }

    public void afiseaza(List<Utilizator> lista){
        friendsObs.clear();
        userObs.clear();
        friendsObs.setAll(this.getPrietenie());
        userObs.setAll(lista);
        userList.setItems(userObs);
        friendshipList.setItems(friendsObs);
    }

    public void afiseazaMsj(List<Mesaj> lista){
        msjObs.clear();
        msjObs.setAll(lista);
        mesajeList.setItems(msjObs);
    }

    public void displayErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public void displayRezultat(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public void handleAddUser(ActionEvent actionEvent) {
        String nume= nume1.getText();
        String prenume= prenume1.getText();
        try {
            Service.getInstance().adaugaUtilizator(nume,prenume);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la adaugare", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la adaugare", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleUpdateUser(ActionEvent actionEvent) {
        String nume= nume1.getText();
        String prenume= prenume1.getText();
        String numeNou= nume2.getText();
        String prenumeNou= prenume2.getText();
        try {
            Service.getInstance().modificaUtilizator(nume,prenume,numeNou,prenumeNou);
        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la update", e.getMessage());
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
            //displayErrorAlert("Error Occurred", "Eroare la update", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        String nume= nume1.getText();
        String prenume= prenume1.getText();
        try {
            Service.getInstance().stergeUtilizator(nume,prenume);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la stergere", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la stergere", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleAddPrietenie(ActionEvent actionEvent) {
        String numeUser1= nume1.getText();
        String prenumeUser1= prenume1.getText();
        String numeUser2= nume2.getText();
        String prenumeUser2= prenume2.getText();
        try {
            Service.getInstance().adaugaPrieten(numeUser1,prenumeUser1,numeUser2,prenumeUser2);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la adaugare", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la adaugare", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleDeletePrietenie(ActionEvent actionEvent) {
        String numeUser1= nume1.getText();
        String prenumeUser1= prenume1.getText();
        String numeUser2= nume2.getText();
        String prenumeUser2= prenume2.getText();
        try {
            Service.getInstance().stergePrieten(numeUser1,prenumeUser1,numeUser2,prenumeUser2);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la stergere", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la stergere", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleComunitateSociabila(ActionEvent actionEvent) {
        try{
            this.displayRezultat("Info","Rezultat","Membrii din cea mai activa comunitate: ");
            ArrayList<ArrayList<Utilizator>> result = Service.getInstance().comunitateSociabila();
            afiseaza(Service.getInstance().celMaiMareArray(result));
        }catch (Exception e){
            this.displayErrorAlert("Error Occurred", "Eroare comunitate sociabila", e.getMessage());
        }
    }

    public void handleNrComunitati(ActionEvent actionEvent) {
        try{
            Integer numar_comunitati = Service.getInstance().nrComunitati();
            this.displayRezultat("Info","Rezultat","Nr de comunitati: "+numar_comunitati);
        }catch (Exception e){
            this.displayErrorAlert("Error Occurred", "Eroare nr de comunitati", e.getMessage());
        }
    }

    public void handleTrimiteMesaj(ActionEvent actionEvent) {
        try {
            String numeUser1 = nume1.getText();
            String prenumeUser1 = prenume1.getText();
            String numeUser2 = nume2.getText();
            String prenumeUser2 = prenume2.getText();
            String text = mesaj.getText();
            String replystr = reply.getText();
            Long reply;
            if (replystr.isEmpty()) {
                reply=-1L;
            } else {
                reply = Long.parseLong(replystr);
            }
            Service.getInstance().trimiteMesaj(numeUser1,prenumeUser1,numeUser2,prenumeUser2,text,reply);
        }
        catch(ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare trimitere mesaj", e.getMessage());
        }
        catch(Exception e){
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare trimitere mesaj", e.getMessage());
        }
        this.actualizeazaMsj();
    }

    public void handleConversatie(ActionEvent actionEvent) {
        try {
            String numeUser1 = nume1.getText();
            String prenumeUser1 = prenume1.getText();
            String numeUser2 = nume2.getText();
            String prenumeUser2 = prenume2.getText();
            this.afiseazaMsj(Service.getInstance().conversatie(numeUser1,prenumeUser1,numeUser2,prenumeUser2));
        }
        catch(ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare conversatie", e.getMessage());
        }
        catch(Exception e){
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare conversatie", e.getMessage());
        }
    }

    public void handleRefresh(ActionEvent actionEvent) {
        this.actualizeaza();
    }

    public void handleExitConv(ActionEvent actionEvent) {
        this.actualizeazaMsj();
    }

    public void handleTrimiteMesajMaiMultiUser(ActionEvent actionEvent) {
        try {
            String numeUser1 = nume1.getText();
            String prenumeUser1 = prenume1.getText();
            String iduri = str.getText();
            String text = mesaj.getText();
            String replystr = reply.getText();
            Long reply;
            if (replystr.isEmpty()) {
                reply=-1L;
            } else {
                reply = Long.parseLong(replystr);
            }
            Service.getInstance().forwardMesaj(numeUser1,prenumeUser1,iduri,text,reply);
        }
        catch(ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare trimitere mesaj", e.getMessage());
        }
        catch(Exception e){
            //System.out.println("Eroare: " + e.getMessage());
            this.displayErrorAlert("Error Occurred", "Eroare trimitere mesaj", e.getMessage());
        }
        this.actualizeazaMsj();
    }

    public void handleTrimiteCerere(ActionEvent actionEvent) {
        String numeUser1= nume1.getText();
        String prenumeUser1= prenume1.getText();
        String numeUser2= nume2.getText();
        String prenumeUser2= prenume2.getText();
        try {
            Service.getInstance().creeazaCerere(numeUser1,prenumeUser1,numeUser2,prenumeUser2);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la trimitere cerere", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la trimitere cerere", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleAcceptaCerere(ActionEvent actionEvent) {
        String numeUser1= nume1.getText();
        String prenumeUser1= prenume1.getText();
        String numeUser2= nume2.getText();
        String prenumeUser2= prenume2.getText();
        try {
            Service.getInstance().acceptaCerere(numeUser1,prenumeUser1,numeUser2,prenumeUser2);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la acceptare cerere", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la acceptare cerere", e.getMessage());
        }
        this.actualizeaza();
    }

    public void handleRespingeCerere(ActionEvent actionEvent) {
        String numeUser1= nume1.getText();
        String prenumeUser1= prenume1.getText();
        String numeUser2= nume2.getText();
        String prenumeUser2= prenume2.getText();
        try {
            Service.getInstance().stergeCerere(numeUser1,prenumeUser1,numeUser2,prenumeUser2);

        } catch (ValidationException e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la respingere cerere", e.getMessage());
        } catch (Exception e) {
            //System.out.println("Eroare: " + e.getMessage());
            displayErrorAlert("Error Occurred", "Eroare la respingere cerere", e.getMessage());
        }
        this.actualizeaza();
    }
}