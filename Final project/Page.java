package Project;

import java.util.ArrayList;

public class Page {

    public int outgoingPages=0;
    public double pageRank=1;
    public int location=0;
    public ArrayList<Integer> incomingPageList = new ArrayList<Integer>();
    public ArrayList<Integer> outgoingPageList = new ArrayList<Integer>();

    public Page(int loc){
	location=loc;
    }
    
    public void addIncomingPage(int x){
	if (!incomingPageList.contains(x)){
		incomingPageList.add(x);	    
	}
    }
    
    public void addOutgoingPage(int x){
	if (!outgoingPageList.contains(x)){
	    outgoingPageList.add(x);	
	    outgoingPages++;
	}
	
    }
    
    public int getNumOutgoing(){
	return outgoingPages;
    }
    public ArrayList<Integer> getOutgoingPageList(){
   	return outgoingPageList;
       }
    public ArrayList<Integer> getIncomingPageList(){
	return incomingPageList;
    }
    
    public int getPage(){
	return location;
    }
    
    public double setPageRank(double p){
	double difference = pageRank-p;
	if(difference<0){
	    difference=difference*-1;
	}
	pageRank=p;
	return difference;
    }
    
    public double getPageRank(){
	return pageRank;
    }

}