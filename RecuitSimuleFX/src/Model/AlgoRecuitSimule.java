package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AlgoRecuitSimule {
	
	HashMap<Integer, City> hashMapCities;
	
	public AlgoRecuitSimule(HashMap<Integer, City> cities) {
		this.hashMapCities = (HashMap<Integer, City>) cities.clone();
		size = this.hashMapCities.size();
		distances = new double[size][size];
		solution = new int[size][size];
		tempSolution = new int[size][size];
	}
	
	// DECLARATION OF THE VARIABLES
	
	int size; // matrix size
	double temperature = 1000; // T = T0
	double nbrIterPalier = 20;// iter_palier
	double coolingFactor = 0.9; // coeff
	int count = 0; // counter
	double toleratedAcceptanceRate = 0.3; // alpha
	int stoppingNumber = 15; // stopping point
	
	// Matrice des distances entre villes
	double[][] distances;
	
	// Matrice des arretes qui forment la solution
	int[][] solution;
	double solutionLength;
	ArrayList<Pair> listSolution;
	
	// Solution temporaire correspondant au palier en cours
	int[][] tempSolution;
	double tempSolutionLength;
	ArrayList<Pair> tempListSolution;
	
	public double getSolutionLength() {
		return this.solutionLength;
	}
	
	public ArrayList<Pair> solve() {
		// generate a graph and a solution
		generateSymetricGraph();
		generateNaiveSolution();
		
		int nbrMoves;
		double acceptanceRate;
		double diffSolLength;
		
		while( count < stoppingNumber) {
			System.out.println("*********** DEBUT PALIER ************");
			System.out.println("Temperature actuelle = " + temperature);
			System.out.println("BEST LENGTH : " + solutionLength);
			
			double previousLength = solutionLength;
			nbrMoves = 0;
			acceptanceRate = 0.0;
			
			// We work on a temporary solution if we want to go back to the previous solution
			tempSolution = solution.clone();
			
			for(int i=0; i<nbrIterPalier; i++) {
				// We randomly choose 2 different cities to swap in the graph
				int randomC1 = ThreadLocalRandom.current().nextInt(0, size);
				int randomC2 = ThreadLocalRandom.current().nextInt(0, size);
				if (randomC1 == randomC2 ) {
					if (randomC2 > 0) {
						randomC2 -= 1;
					}
					else {
						randomC2 += 1;
					}
				}
				swapTravels(randomC1, randomC2);
				updateTempSolution();
				// We accept or reject the solution based on the metropolis criteria
				diffSolLength = solutionLength - tempSolutionLength;
		
				if (metropolisCriteria(temperature, diffSolLength)) {
					acceptTempSolution();
					nbrMoves++;
					System.out.println("Solution : " + tempSolutionLength + " acceptee");
				}
				else {
					rejectTempSolution();
				}
			}
			// End of a palier
			acceptanceRate = nbrMoves / nbrIterPalier;
			System.out.println("Acceptance rate = "  + acceptanceRate);
			if (acceptanceRate <= toleratedAcceptanceRate) {
				count++;
			}
			if (solutionLength > previousLength) {
				count = 0;
			}
			temperature = temperature*coolingFactor;
		}
		System.out.println("***********************************************");
		System.out.println("***********************THE*********************");
		System.out.println("***********************END*********************");
		System.out.println("***********************************************");
		System.out.println("The Best Solution is : ");
		System.out.println("Chemin : " + listSolution.toString() + " de longueur : " + solutionLength);
		return listSolution;
	}
	
	
	// AFFICHER UNE MATRICE
	public void printMatrix(double[][] matrix, int mSize) {
		for (int i = 0; i < mSize; i++) {
			for (int j = 0; j < mSize; j++) {
				System.out.print("[ " + matrix[i][j] +" ]");
			}
			System.out.println();
		}
	}
	
	// GENERER UN GRAPH
	// A mettre dans une autre classe HamiltonianGraph
	public void generateSymetricGraph() {
		
		for (int i = 0; i < size; i++) {
			distances[i][i] = 0;
			for (int j = 0; j < size; j++) {
				if(j != i) {
				// nextInt is normally exclusive of the top value
				//int randomNum = ThreadLocalRandom.current().nextInt(5, 40);
				double distanceIJ = this.distance(this.hashMapCities.get(i+1), this.hashMapCities.get(j+1)); //We take i+1 and j+1 because id of the cities begin at 1.
				distances[i][j] = distanceIJ; //Was previously randomNum
				distances[j][i] = distanceIJ; //Was previously randomNum
				}
			}
		}
		System.out.println("Matrice des distances entre villes : ");
		printMatrix(distances, size);
	}
	
	// GENERE SOLUTION SIMPLE
	public void generateNaiveSolution() {
		solutionLength = 0;
		// Initialize with 0 everywhere
		for (int[] row : solution) {
			Arrays.fill(row, 0);
		}
		// Add a simple solution (here just following the order of IDs)
		for (int i = 0; i < size-1; i++) {
			solution[i][i+1] = 1;
			solution[i+1][i] = 1;
			solutionLength += distances[i][i+1];
		}
		solution[size-1][0] = 1;
		solution[0][size-1] = 1;
		solutionLength += distances[size-1][0];
		updateSolution();
	}
	
	// METTRE A JOUR LA LONGUEUR DE LA SOLUTION ET AFFICHER LE CHEMIN
	public void updateSolution() {
		ArrayList<Pair> listSol = new ArrayList<Pair>();
		solutionLength = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j <i; j++) {
				if (solution[i][j] == 1) {
					listSol.add(new Pair(i, j));
					solutionLength += distances[i][j];
				}
			}
		}
		listSolution = listSol;
		System.out.println("Chemin : " + listSolution.toString() + " de longueur : " + solutionLength);
	}
	
	// METTRE A JOUR LA LONGUEUR DE LA SOLUTION TEMPORAIRE ET AFFICHER LE CHEMIN
	public void updateTempSolution() {
		ArrayList<Pair> listSol = new ArrayList<Pair>();
		tempSolutionLength = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j <i; j++) {
				if (tempSolution[i][j] == 1) {
					listSol.add(new Pair(i, j));
					tempSolutionLength += distances[i][j];
				}
			}
		}
		tempListSolution = listSol;
		System.out.println("Chemin : " + tempListSolution.toString() + " de longueur : " + tempSolutionLength);
	}	
	
	
	// Swap 2 cities in a matrix representing an hamiltonian cycle in a graph
	public void swapTravels(int city1, int city2) {
		// we work on a temporary clone of the solution in case of a need to revert back to previous solution
		// for example if the solution is too bad at the end
	
		// 1) Find neighbours that are linked to the city we want to swap 
		//    by going through the solution matrix
		ArrayList<Integer> c1Neighbours = new ArrayList<Integer>();
		ArrayList<Integer> c2Neighbours = new ArrayList<Integer>();
		System.out.println("Swapping : " + city1 + " et " + city2 );
		
			for (int j = 0; j < size; j++) {

				boolean isC1Neighbour = tempSolution[city1][j] == 1;
				boolean isC2Neighbour = tempSolution[city2][j] == 1;
				
		// If they have neighbours in common no need to swap those links
				if (isC1Neighbour && !isC2Neighbour) {
					c1Neighbours.add(j);
				}
				
				if(isC2Neighbour && !isC1Neighbour) {
					c2Neighbours.add(j);
				}
			
			}
			
		// 2) Swap the cities in the solution matrix
			for ( int n : c1Neighbours) {
				// remove the previous links
				tempSolution[city1][n] = 0;
				tempSolution[n][city1] = 0;
				
				// Problem if city 1 and city 2 are direct neighbours
				if(n == city2) {
					tempSolution[city2][city1] = 1;
					tempSolution[city1][city2] = 1;
				}
				else {
				// add the new links
					tempSolution[city2][n] = 1;
					tempSolution[n][city2] = 1;
				}
			}

			for ( int m : c2Neighbours) {
				// remove the previous links
				tempSolution[city2][m] = 0;
				tempSolution[m][city2] = 0;
				
				// Problem if city 1 and city 2 are direct neighbours
				if(m == city1) {
					tempSolution[city1][city2] = 1;
					tempSolution[city2][city1] = 1;
				}
				else {
				// add the new links
					tempSolution[city1][m] = 1;
					tempSolution[m][city1] = 1;
				}
			}
			
	}
	
	// A OPTIMISER PROBABLEMENT
	public void acceptTempSolution() {
		solution = tempSolution.clone();
		solutionLength = tempSolutionLength;
		listSolution = tempListSolution;
	}
	// A OPTIMISER PROBABLEMENT
	public void rejectTempSolution() {
		tempSolution = solution.clone();
		tempSolutionLength = solutionLength;
		tempListSolution = listSolution;
	}
	
	// Evaluate the acceptability of a solution
	public boolean metropolisCriteria(double temp, double delta) {
		if(delta>=0) {
			return true;
		}
		else{
			double randomNum = ThreadLocalRandom.current().nextDouble(0, 1);
			boolean accepted = Math.exp(delta/temp) > randomNum;
			return accepted;
		}
	}
	
	// Update the end condition depending on what happened during a "palier"
	public void updateCount(double acceptRate, boolean hasInreased) {
		if (acceptRate < toleratedAcceptanceRate) {
			count++;
		}
		if (hasInreased){
			count = 0;
		}
	}
	
	public double distance(City c1, City c2)
	{
		return Math.sqrt(Math.pow((c2.getX()-c1.getX()), 2) + Math.pow((c2.getY()-c1.getY()), 2));
	}
}

