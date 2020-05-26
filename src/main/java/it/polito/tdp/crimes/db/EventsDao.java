package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	/**
	 * Restituisce una lista di tutti gli eventi e riempie una idMap
	 * @param eventMap
	 * @return lista di tutti gli eventi
	 */
	public List<Event> listAllEvents(Map<Long,Event> eventMap){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Event e=new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic"));
					list.add(e);
					eventMap.put(e.getIncident_id(), e);
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String offense_category_id, Month mese) {
		String sql="SELECT e1.offense_type_id as v1, e2.offense_type_id as v2, COUNT(DISTINCT(e1.neighborhood_id)) as peso " + 
			"FROM events e1, events e2 " + 
			"WHERE e1.offense_category_id=? AND e2.offense_category_id=? "+
			"AND MONTH(e1.reported_date)=? AND MONTH(e2.reported_date)=? AND e1.offense_type_id!=e2.offense_type_id "+
			"AND e1.neighborhood_id=e2.neighborhood_id " + 
			"GROUP BY e1.offense_type_id, e2.offense_type_id";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Adiacenza> adiacenze = new ArrayList<>() ;
			st.setString(1, offense_category_id);
			st.setString(2, offense_category_id);
			st.setInt(3, mese.get(ChronoField.MONTH_OF_YEAR));
			st.setInt(4, mese.get(ChronoField.MONTH_OF_YEAR));
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					adiacenze.add(new Adiacenza(res.getString("v1"), res.getString("v2"), res.getDouble("peso")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return adiacenze;
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}

}
