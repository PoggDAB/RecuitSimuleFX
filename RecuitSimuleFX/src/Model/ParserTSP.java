package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ParserTSP {
	
	public static HashMap<Integer, City> read(String fileLocation) throws FileNotFoundException {
		File file = new File(fileLocation);
		Scanner sc = new Scanner(file);
		String nextValue = null;
		//ArrayList<City> listCities = new ArrayList<>();
		HashMap<Integer, City> hashMapCities = new HashMap<>();
		while(sc.hasNextLine())
		{
			String line = sc.nextLine();
			if(line.equals("NODE_COORD_SECTION")) 
			{
				while (sc.hasNextLine())
				{
					nextValue = sc.nextLine();
					
					if(nextValue.equals("EOF")) break;
					
					String[] tempTab = nextValue.trim().split(" ");
					ArrayList<Double> listWithValuesWithoutSpaces = new ArrayList<>();
					
					//to ignore the case with ""
					for(int i=0; i<tempTab.length; i++) 
					{
						if(!tempTab[i].isEmpty())
						{
							listWithValuesWithoutSpaces.add(Double.parseDouble(tempTab[i]));
						}
					}
					
					City c = new City(listWithValuesWithoutSpaces.get(0).intValue(), listWithValuesWithoutSpaces.get(1), listWithValuesWithoutSpaces.get(2));
					hashMapCities.put(c.getId(), c);
				}
			}
		}
		sc.close();
		return hashMapCities;
	}

}
