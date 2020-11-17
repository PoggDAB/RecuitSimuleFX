import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.xerces.parsers.*;
import org.w3c.dom.*;

import ilog.concert.*;
import ilog.cplex.*;

public class Traveler {

	public static void main(String[] args) {
		
		Document document = null;
	    DOMParser parser = null;
	    City[] listCity = null;
	    
	    //Dans un 1er temps on remplis un tableau de données qui contiendra toutes les villes
	    try {
	      parser = new DOMParser();
	      parser.parse("a280.xml"); // Nom du fichier à traiter
	      document = parser.getDocument();
	      
	      NodeList vertexList =  document.getElementsByTagName("vertex");
	      
	      listCity = new City[vertexList.getLength()]; //Tableau de données qui contiendra toutes les villes
	      
	      int y = 0;
	      for (y = 0; y < vertexList.getLength() ; y++) { // Parcours de toutes les villes (vertex)
	    	  
	  		//Instanciation ville & HashMap
	  		City c1 = new City(); 
	  		c1.name = y;
	  		HashMap<String, Integer> mapCity = new HashMap<String, Integer>();
	  		
	  		// Pour chaque vertex on recupere la liste des noeuds enfants :
			Node vertex1 =  vertexList.item(y);
			NodeList costList = vertex1.getChildNodes();
			
			for (int i = 0; i < costList.getLength() ; i++) { // Parcours de tout les couts relatif a chaque ville pour la ville "y"
	    	  
	    	  Node cost = costList.item(i);
	    	  
	    	  // Le fichier XML semble contenir des balises vides 1 fois sur 2, on s'assure donc de ne pas les traiter :
	    	  if (cost.getNodeType() == Node.ELEMENT_NODE && cost.getTextContent() != null) { 
	    		  Element eCost = (Element) cost; // On utilise Element pour pouvoir acceder aux données de la balise
	    		  mapCity.put(cost.getTextContent(), (int) Double.parseDouble(eCost.getAttribute("cost")));
	    	  
	    	  }
	    	  c1.mapCost = mapCity; // Remplissage de la HashMap de la ville y
	      }
		      listCity[y] = c1; // Ajout de la ville dans le tableau de données
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    
	    
	    try {
			IloCplex cplex = new IloCplex();
			
		    IloNumVar[][] x = new IloNumVar[listCity.length][];
		    
		    for (int i = 0; i<listCity.length; i++) {
		    	
		    	x[i] = cplex.boolVarArray(listCity.length);
		    	
		    }
		    
		    IloNumVar[] u = cplex.numVarArray(listCity.length, 0, Double.MAX_VALUE);
		    
		    //Fonction objective		    
		    IloLinearNumExpr obj = cplex.linearNumExpr();
		    
		    for (int i = 0; i< listCity.length; i++) {
		    	
		    	for (int y = 0; y< listCity.length; y++) {
			    	
			    	if (y != i) {
			    		obj.addTerm(listCity[i].mapCost.get(Integer.toString(y)), x[i][y]);
			    	}
			    }
		    }
		    cplex.addMinimize(obj);
		    
		    //Subject to
		    
		    //1ere contrainte
		    for (int j = 0; j< listCity.length; j++) {
		    	
		    	IloLinearNumExpr expr = cplex.linearNumExpr();
		    	for (int i = 0; i<listCity.length;i++ ) {
		    		if (i!=j) {
		    			expr.addTerm(1.0, x[i][j]);
		    		}
		    	}
		    	cplex.addEq(expr, 1.0);
		    }
		    
		    //2eme contrainte
		    for (int i = 0; i< listCity.length; i++) {
		    	
		    	IloLinearNumExpr expr = cplex.linearNumExpr();
		    	for (int j = 0; j<listCity.length;j++ ) {
		    		if (i!=j) {
		    			expr.addTerm(1.0, x[i][j]);
		    		}
		    	}
		    	cplex.addEq(expr, 1.0);
		    }
		    
		    
		    for (int i = 1; i< listCity.length; i++) {
			    for (int j = 1; j< listCity.length; j++) {
			    	
			    	if (i!=j) {
				    	IloLinearNumExpr expr = cplex.linearNumExpr();
				    	
				    	expr.addTerm(1.0, u[i]);
				    	expr.addTerm(-1.0, u[j]);
				    	expr.addTerm(listCity.length-1, x[i][j]);
				    	cplex.addLe(expr, listCity.length-2);
		    			
		    		}
			    }
		    }
		    
		    cplex.solve();
		    
		    cplex.end();
		    
		} catch (IloException e1) {
			e1.printStackTrace();
		}
	    }
	}
