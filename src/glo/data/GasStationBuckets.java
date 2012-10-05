package glo.data;

import glo.sys.GLSettings;
import glo.types.GasCompany;

import java.util.Vector;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

public class GasStationBuckets extends Vector implements Comparator {
	
	private final static GasStationBuckets buckets = new GasStationBuckets();
	
	private int groupStationsWithin = -1;
	
	/**
	 * Takes a list of gas stations and sorts them and returns an array of them. 
	 * @param stations
	 * @param sortMode
	 * @return
	 */
	public static GasCompany [] getSortedStations(GasCompany [] stations, int sortMode){
		buckets.groupStationsWithin = GLSettings.getGroupStationsWithin(); // keep this value for the duration of the sort
		if(sortMode == GLSettings.SORT_BY_BOTH){
			// add all the gas stations to a bucket
			buckets.removeAllElements(); 
			for(int i = 0; i < stations.length; i++){
				buckets.addToBucket(stations[i]);
			}
			
			// now sort each bucket
			for(int j = 0; j < buckets.size(); j++){
				GasStationBucket bucket = (GasStationBucket) buckets.elementAt(j);
				bucket.sort();
			}
			
			// now sort the list of buckets
			buckets.sort();
			return buckets.getStations();
		}
		
		return stations;
	}
	
	
	private void addToBucket(GasCompany gasCompany) {
		if(gasCompany == null) return;
		int whichBucket = ((int) (gasCompany.getDistanceFromUser() / buckets.groupStationsWithin)) * buckets.groupStationsWithin;
		GasStationBucket bucket = getBucket(whichBucket, true);
		bucket.addElement(gasCompany);
	}
	
	/**
	 * Returns true if a bucket exists
	 * @param startValue
	 * @return
	 */
	private boolean bucketExists(int startValue){
		return getBucket(startValue, false) == null;
	}
	
	private GasStationBucket getBucket(int startValue, boolean createBucket){
		for(int i = 0; i < size(); i++){
			GasStationBucket bucket = (GasStationBucket) elementAt(i);
			if(bucket.getStartValue() == startValue){
				return bucket;
			}
		}
		
		if(createBucket){
			GasStationBucket bucket = new GasStationBucket(startValue, startValue + groupStationsWithin);
			addElement(bucket);
			return bucket;
		} else {
			return null;
		}
	
	}
	
	public void sort(){
		GasStationBucket [] buckets = new GasStationBucket [size()];
		for(int i = 0; i < size(); i++){
			buckets[i] = (GasStationBucket) elementAt(i);
		}
		
		Arrays.sort(buckets, this);
		
		removeAllElements();
		for(int j = 0; j < buckets.length; j++){
			addElement(buckets[j]);
		}
		
		// now you can access the buckets
		
	}
	
	public GasCompany [] getStations (){
		Vector stations = new Vector();
		for(int i = 0; i < size(); i++){
			GasStationBucket bucket = (GasStationBucket) elementAt(i);
			for(int j = 0; j < bucket.size(); j++){
				GasCompany station = (GasCompany) bucket.elementAt(j);
				stations.addElement(station);
			}
		}
		
		GasCompany [] stationsArr = new GasCompany[stations.size()];
		for(int k = 0; k < stations.size(); k++){
			stationsArr[k] = (GasCompany) stations.elementAt(k);
		}
		
		return stationsArr;
	}


	public int compare(Object o1, Object o2) {
		GasStationBucket b1 = (GasStationBucket) o1;
		GasStationBucket b2 = (GasStationBucket) o2;
		
		if(b1.getStartValue() > b2.getStartValue()){
			return 1;
		} else if(b1.getStartValue() < b2.getStartValue()){
			return -1;
		}
		return 0;
	}
	
}
