package it.polito.tdp.crimes.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private Graph<String, DefaultWeightedEdge> grafo;
	private Map<Long, Event> eventMap=new HashMap<>();
	private EventsDao dao= new EventsDao();
	private List<String> bestPath;
	
	 public Map<Long,Event> listAllEvent(){
		 dao.listAllEvents(eventMap);
		 return eventMap;
	 }
	
	 //creo il grafo
	 public void creaGrafo(String offense_category_id,Month mese) {
		 //grafo semplice pesato non orientato
		 //i vertici sono i tipi di reato offense_type_id
		this.grafo=new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		List<Adiacenza> adiacenze= this.dao.getAdiacenze(offense_category_id,mese);
		for(Adiacenza a:adiacenze) {
			if(!this.grafo.containsVertex(a.getV1())) {
				this.grafo.addVertex(a.getV1());
			}
			if(!this.grafo.containsVertex(a.getV2())) {
				this.grafo.addVertex(a.getV2());
			}
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null && a.getPeso()>0) {
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		System.out.println(String.format("Grafo creato con %d vertici e %d archi", this.grafo.vertexSet().size(),this.grafo.edgeSet().size()));
	 }
	 
	 public List<Adiacenza> getArchi(){
		 double pesoMedio=0;
		 for(DefaultWeightedEdge edge: this.grafo.edgeSet()) {
			 pesoMedio=this.grafo.getEdgeWeight(edge);
		 }
		 pesoMedio=pesoMedio/this.grafo.edgeSet().size();
		 List<Adiacenza> maggiori=new ArrayList<>();
		 for(DefaultWeightedEdge edge:this.grafo.edgeSet()) {
			 if(this.grafo.getEdgeWeight(edge)>pesoMedio)
				 maggiori.add(new Adiacenza(this.grafo.getEdgeSource(edge),this.grafo.getEdgeTarget(edge), this.grafo.getEdgeWeight(edge)));
		 }
		 Collections.sort(maggiori);
		 return maggiori;
	 }
	 public List<String> trovaPercorso(String sorgente, String destinazione){
		 List<String> parziale= new ArrayList<>();
		 this.bestPath=new ArrayList<>();
		 parziale.add(sorgente);
		 trovaRicorsivo(destinazione,parziale,0);
		 return bestPath;
	 }

	private void trovaRicorsivo(String destinazione, List<String> parziale, int livello) {
		//caso terminale?
		//quando l'ultimo vertice inserito in parziale è destinazione
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size()>bestPath.size())
				this.bestPath=new ArrayList<>(parziale);
			return;
		}
		//scorro i vicini dell'ultimo vertice inserito in parziale
		for(String vicino:Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			//cammino aciclico-->controllo che il vertice non sia già in parziale
			if(!parziale.contains(vicino)) {
				//provo ad aggiungere
				parziale.add(vicino);
				//continuo la ricorsione
				this.trovaRicorsivo(destinazione, parziale, livello+1);
				//faccio backtracking
				parziale.remove(parziale.size()-1);
			}
		}
	}
}
