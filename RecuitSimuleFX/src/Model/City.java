package Model;

import java.util.ArrayList;
import java.util.Map;

public class City {
	private int _id;
	private double _x, _y;
	//private Map<Integer, Double> _neighbours;
	
	public City(int id, double x, double y) {
		this._id = id;
		this._x = x;
		this._y = y;
	}
	
	public double getX() {
		return this._x;
	}
	
	public double getY() {
		return this._y;
	}
	
	public int getId() {
		return this._id;
	}
	
	public String toString() {
		String stringCity = "City n°" + this._id + ", x : " + this._x + ", y : " + this._y; 
		return stringCity;
	}
}
