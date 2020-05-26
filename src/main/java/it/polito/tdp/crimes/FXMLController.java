/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;
import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Month> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Adiacenza> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Adiacenza a=this.boxArco.getValue();
    	if(a==null) {
    		this.txtResult.appendText("SELEZIONARE UN ARCO!");
    		return;
    	}
    	List<String> percorso=this.model.trovaPercorso(a.getV1(), a.getV2());
    	txtResult.appendText("Percorso migliore:\n");
    	for(String v:percorso) {
    		txtResult.appendText(v+"\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String offense=this.boxCategoria.getValue();
    	if(offense==null) {
    		txtResult.appendText("SELEZIONA CATEGORIA!");
    		return;
    	}
    	Month mese=this.boxMese.getValue();
    	if(mese==null) {
    		txtResult.appendText("SELEZIONA MESE!");
    		return;
    	}
    	this.model.creaGrafo(offense, mese);
    	List<Adiacenza> archi=this.model.getArchi();
    	this.boxArco.getItems().addAll(archi);
    	txtResult.appendText("Archi con peso maggiore del peso medio:\n");
    	for(Adiacenza a:archi) {
    		txtResult.appendText(a.toString()+"\n");
    	}
    }
    void loadData() {
    	List<String> categoryList = new LinkedList<>();
    	List<Month> mesi= new LinkedList<>();
    	for(Event e:model.listAllEvent().values()) {
    		if(!categoryList.contains(e.getOffense_category_id()))
    			categoryList.add(e.getOffense_category_id());
    		if(!mesi.contains(e.getReported_date().getMonth())) 
    			mesi.add(e.getReported_date().getMonth());
    	}
    	Collections.sort(mesi);
    	this.boxCategoria.getItems().addAll(categoryList);
    	this.boxMese.getItems().addAll(mesi);
    }
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	loadData();
    }
}
