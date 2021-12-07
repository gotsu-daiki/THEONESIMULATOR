/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package core;

import java.util.List;
import movement.map.MapNode;
/**
 * Class to hold 2D coordinates and perform simple arithmetics and
 * transformations
 */
public class Coord implements Cloneable, Comparable<Coord> {
	private static final String Coord = null;
	private double x;
	private double y;
	
	/**
	 * Constructor.
	 * @param x Initial X-coordinate
	 * @param y Initial Y-coordinate
	 */
	public Coord(double x, double y) {
		setLocation(x,y);
	}
	
	/**
	 * Sets the location of this coordinate object
	 * @param x The x coordinate to set
	 * @param y The y coordinate to set
	 */
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;		
	}
	
	
	
	/**
	 * Sets this coordinate's location to be equal to other
	 * coordinates location
	 * @param c The other coordinate
	 */
	public void setLocation(Coord c) {
		this.x = c.x;
		this.y = c.y;		
	}
	
	/**
	 * Moves the point by dx and dy
	 * @param dx How much to move the point in X-direction
	 * @param dy How much to move the point in Y-direction
	 */
	public void translate(double dx, double dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/**
	 * Returns the distance to another coordinate
	 * @param other The other coordinate
	 * @return The distance between this and another coordinate
	 */
	public double distance(Coord other) {
		double dx = this.x - other.x;
		double dy = this.y - other.y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Returns the x coordinate
	 * @return x coordinate
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Returns the y coordinate
	 * @return y coordinate
	 */	
	public double getY() {
		return this.y;
	}
	//座標位置をdouble型の配列で返すメソッド
	public double[] Intgetcoords() {
		double a[]= {this.x,this.y};
		return a;
	}
	
	/**
	 * Returns a text representation of the coordinate (rounded to 2 decimals)
	 * @return a text representation of the coordinate
	 */
	public String toString() {
		return String.format("(%.2f,%.2f)",x,y);
	}
	
	/**
	 * Returns a clone of this coordinate
	 */
	public Coord clone() {
		Coord clone = null;
		try {
			clone = (Coord) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return clone;
	}
	
	/**
	 * Checks if this coordinate's location is equal to other coordinate's
	 * @param c The other coordinate
	 * @return True if locations are the same
	 */
	public boolean equals(Coord c) {
		if (c == this) {
			return true;
		}
		else {
			return (x == c.x && y == c.y); // XXX: == for doubles...
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		return equals((Coord) o);
	}

	/**
	 * Returns a hash code for this coordinate
	 * (actually a hash of the String made of the coordinates)
	 */
	public int hashCode() {
		return (x+","+y).hashCode();
	}

	/**
	 * Compares this coordinate to other coordinate. Coordinate whose y
	 * value is smaller comes first and if y values are equal, the one with
	 * smaller x value comes first.
	 * @return -1, 0 or 1 if this node is before, in the same place or
	 * after the other coordinate
	 */
	public int compareTo(Coord other) {
		if (this.y < other.y) {
			return -1;
		}
		else if (this.y > other.y) {
			return 1;
		}
		else if (this.x < other.x) {
			return -1;
		}
		else if (this.x > other.x) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * 座標a,bが整数部分が一致しているかを返すメソッド
	 * @param a
	 * @param b
	 * @return aとｂのX,Y座標の整数部分を比較し、等しければtrue,そうでなければfalse
	 */
	public static boolean CompareIntEqual(Coord a,Coord b) {
		
		if((int)a.getX()==(int)b.getX()) {
			if((int)a.getY()==(int)b.getY()){
				return true;
			}
	}
		return false;
}

	/**
	 * 座標a,bが一致しているかを返すメソッド
	 * @param a
	 * @param b
	 * @return aとｂのX,Y座標を比較し、等しければtrue,そうでなければfalse
	 */
public static boolean CompareEqual(Coord a,Coord b) {
		
		if(a.getX()==b.getX()) {
			if(a.getY()==b.getY()){
				return true;
			}
	}
		return false;
}

/**
 * listの中にcoordがあるかどうか判別
 * @param Arraylist list
 * @param Coord　coord
 * @return 
 */
public static boolean containsIntlocation(List<Coord>list,Coord coord) {
		int i=0;
		for(Coord COORD:list) {
			if(CompareIntEqual(COORD, coord)){
				return true;
			}
		i++;
		}	
		return false;	
	}

/**
 * listの中にcoordがあるかどうか判別
 * @param Arraylist list
 * @param Coord　coord
 * @return 
 */
public static boolean containsIntlocation2(List<MapNode>list,Coord coord) {
		for(MapNode COORD:list) {
			if(CompareIntEqual(COORD.location, coord)){
				return true;
			}
		}	
		return false;	
	}
/**
 * listの中にcoordが含まれいているかどうか
 * @param Arraylist list　マップノード
 * @param Coord　coord
 * @return 　　含まれていればtrue,そうでなければfalse
 */
public static Boolean PathContainsDisasterNode(List<MapNode>path,List<Coord>avoidance,DTNHost host) {
	int i,n;
	n=path.size();
	for(Coord COORD:avoidance) {
		for(i=0;i+1<n;i++) {
		 //disaster座標がパスのi番目とi+1番目の間にあるかどうか判断
			if(path.get(i+1).location.getX()>path.get(i).location.getX()&&COORD.getX()>=path.get(i).location.getX()&&COORD.getX()<=path.get(i+1).location.getX()||path.get(i+1).location.getX()<path.get(i).location.getX()&&COORD.getX()<=path.get(i).location.getX()&&COORD.getX()>=path.get(i+1).location.getX()) {
				
			    double a=((path.get(i+1).location.getY()-path.get(i).location.getY())/(path.get(i+1).location.getX()-path.get(i).location.getX()));
			    double b=path.get(i).location.getY()-a*path.get(i).location.getX();
				//if(host.address==418) {
					System.out.println(COORD.getY()+"+"+(a*COORD.getX()+b));
					if((int)(COORD.getY())-a*COORD.getX()+b<=15||COORD.getY()-a*COORD.getX()+b>=-15) {
						
						host.AvoidanceNode.add(path.get(i+1));
						
							return true;//}
	    	 		}			
				}
		}
	}
return false;

}

/*public static Boolean PathContainsAvoidanceNode(List<MapNode>path,List<MapNode>avoidance,DTNHost host) {
	int i,j,n,m;
	n=path.size();
	m=avoidance.size();
	
	
		for(i=0;i+1<n;i++) {
			if(avoidance.contains(path.get(i))) {
				if(avoidance.contains(path.get(i+1))) {
					host.RealAvoidanceNode.add(path.get(i+1));
					return true;
				}
						
						
				else if(i!=0&&avoidance.contains(path.get(i-1))) {
					host.RealAvoidanceNode.add(path.get(i-1));
					return true;
				}
			}
		}
		
		return false;
		
	}*/


public static Boolean PathContainsAvoidanceEdge(List<MapNode>path,List<List<MapNode>>Edge,DTNHost host) {
	int i,m;

	m=Edge.size();
	
		for(i=0;i+1<m;i++) {
			//for(j=0;J+1<n;i++)
			{
			//if(
			//Edge.get(i));
			//host.RealAvoidanceNode.add(Edge.get(i));
			return true;
		    }
	
}return false;
}

			
		
		
		

		
	
	
}





