package glo.data;

import glo.types.GasCompany;

import java.util.Vector;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

/**
 * This class will hold a list of gas station for sorting. With some
 * concepts adopted from bucket sort. Each bucket will store gas stations
 * that fall within a certain distance from the user. The gas station
 * distance must be >= startValue and < endValue. 
 * 
 * @author jay
 *
 */
public class GasStationBucket extends Vector implements Comparator {
	private int startValue = 0;
	
	private int endValue = 0;
	
	
	public GasStationBucket(int startValue, int endValue){
		this.startValue = startValue;
		this.endValue = endValue;
	}
	
	private GasCompany [] getSortedGasStationsAsArray(){
		GasCompany [] stationsArr = new GasCompany[size()];
		for(int i = 0; i < size(); i++){
			stationsArr[i] = (GasCompany) elementAt(i);
		}
		Arrays.sort(stationsArr, this);
		return stationsArr;
	}
	
	public void sort(){
		GasCompany [] stations = getSortedGasStationsAsArray();
		removeAllElements();
		for(int i = 0; i < stations.length; i++){
			addElement(stations[i]);
		}
	}
	
	
	
	public void addStation(GasCompany g){
		if(g.getDistanceFromUser() >= startValue && g.getDistanceFromUser() < endValue){
			if(!contains(g)){
				addElement(g);
			}
		}
	}
	
	
	
	public int compare(Object o1, Object o2){
		GasCompany g1 = (GasCompany) o1;
		GasCompany g2 = (GasCompany) o2;
		
		double g1AvgPrice = g1.getAveragePrices();
		double g2AvgPrice = g2.getAveragePrices();
		
		if(g1AvgPrice > g2AvgPrice){
			return 1;
		} else if(g1AvgPrice < g2AvgPrice){
			return -1;
		} else {
			return 0;
		}
		
	}
	
	public int getStartValue(){
		return startValue;
	}
	
	public int getEndValue(){
		return endValue;
	}
	
}
