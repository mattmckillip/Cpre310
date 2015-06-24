package Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PageRank {


	public static void pageRank(String inputFilename, int numberOfNodes) {
		int[][] textInputArray = null;
		ArrayList<Page> pageList = new ArrayList<Page>();
		ArrayList<Page> reversePageList = new ArrayList<Page>();
		
		try {
			// read in file
			textInputArray = readFile(inputFilename);
		} catch (FileNotFoundException e) {
			System.out.println("404: File Not Found");
		}

		// create pageList and reversePagelist
		for (int j = 1; j <= numberOfNodes + 1; j++) {
			pageList.add(new Page(j));
			reversePageList.add(new Page(j));
		}

		// update incoming and outgoing nodes
		for (int[] i : textInputArray) {
			// make sure no loops are added
			if (i[0] != i[1]) {
				// add connections to both pageList and reversePageList
				reversePageList.get(i[1]).addOutgoingPage(i[0]);
				reversePageList.get(i[0]).addIncomingPage(i[1]);

				pageList.get(i[0]).addOutgoingPage(i[1]);
				pageList.get(i[1]).addIncomingPage(i[0]);
			}
		}
		double[][] allresults=new double[4][];
		
		// run algorithms
		System.out.println("Monte Carlo Simulation Method");
		allresults[2]=monteCarloSimulationMethod(numberOfNodes, pageList);

		System.out.println("");
		allresults[3]=monteCarloSimulationMethod(numberOfNodes, reversePageList);

		System.out.println("\nPower Iteration Method");
		allresults[0]=powerIterationMethod(pageList,numberOfNodes);

		System.out.println("");
		allresults[1]=powerIterationMethod(reversePageList,numberOfNodes);

		writeFile(allresults);
		
	}
	

	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	//				        	Power Iteration Method                        //
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	private static double[] powerIterationMethod(ArrayList<Page> list,int numberOfNodes) {

		double[] resultArr=new double[numberOfNodes+1];
		// if the flag is set, another iteration will be run
		boolean flag = true;

		// variable which will contain the difference between the values in two
		// iterations
		double difference;

		// probability as stated in project description
		double p = .15;

		// calculate page rank
		while (flag) {

			// reset flag
			flag = false;

			// iterate through every page
			for (int i = 1; i < list.size(); i++) {
				ArrayList<Integer> incoming = list.get(i).getIncomingPageList();

				// declare temporary variables
				double PR = 0.0;
				double d = 0.0;
				double sum = 0.0;

				// iterate through the pages which have an incoming link
				for (int j = 0; j < incoming.size(); j++) {
					PR = list.get(incoming.get(j)).getPageRank();
					d = list.get(incoming.get(j)).getNumOutgoing();
					sum += PR / d;
				}

				// set the page rank, this also returns the difference
				difference = list.get(i).setPageRank((1.0 - p) + p * sum);

				// if any difference is above .001 then the iteration is run
				// again
				if (difference > .001) {
					flag = true;
				}
			}
		}

		// print for testing purposes
		for (int i = 1; i < list.size(); i++) {
			resultArr[i]=(double)list.get(i).getPageRank();
			System.out.println((double) Math
					.round(list.get(i).getPageRank() * 10000) / 10000);
		}
		return resultArr;
	}
	
	
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	//						Monte Carlo Simulation Method                     //
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	private static double[] monteCarloSimulationMethod(int numberOfNodes,
			ArrayList<Page> list) {

		// declare random generator
		Random random = new Random();

		// initialize variables
		double alpha = 15;
		double[] simResults = new double[numberOfNodes + 1];
		double[] numOfVisits = new double[numberOfNodes + 1];
		boolean isWalking = true;
		int temp = 0;
		int lengthOfWalk = 0;

		// number of simulations
		int N = 10000000;

		// iterate N times
		for (int i = 0; i < N; i++) {

			// randomly pick the first starting page
			int curPage = random.nextInt(numberOfNodes) + 1;
			
			isWalking = true;
			
			// while flag is set
			while (isWalking) {

				// calculate if it will terminate randomly on this set
				if (alpha < (random.nextInt(100) + 1)) {

					// iterate the length of the walk and the # of visits to
					// current page
					lengthOfWalk++;
					/**
					numOfVisits[curPage] += 1;
					simResults[curPage]=numOfVisits[curPage]/lengthOfWalk;**/

					// if the current page has an outgoing link
					if (list.get(curPage).getNumOutgoing() != 0) {

						// randomly chose an index from outgoing links
						temp = random.nextInt(list.get(curPage)
								.getNumOutgoing());

						// update current node to the randomly chosen outgoing
						// page
						curPage = list.get(curPage).getOutgoingPageList()
								.get(temp);
					}
					else{
						isWalking=false;
					}
				}

				// end trial
				else
					isWalking = false;
			}
			numOfVisits[curPage] += 1.0;
		}

		//find the fraction of runs out of the total that ended at each node
		for (int i = 1; i < numberOfNodes + 1; i++) {
			// print for testing purposes
			simResults[i] = numOfVisits[i] / N;
		}

		for (int i = 1; i < numberOfNodes + 1; i++) {
			// print for testing purposes
			System.out
					.println((double) Math.round(simResults[i] * 10000) / 10000);
		}
		return simResults;
	}

	
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	//						     File Reader                                  //
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	/**
	 * 
	 * @param numberOfNodes
	 *            - number of nodes
	 * @return arr - double int array which contains each node and the relation
	 *         as written in the file
	 * @throws FileNotFoundException
	 * 
	 *             this function takes in the filename. Then parses the file
	 *             into a double int array to be added to pageList and
	 *             reversePageList
	 */
	private static int[][] readFile(String inputFilename)
			throws FileNotFoundException {

		// create a scanner for the file
		File inputFile = new File(inputFilename);
		Scanner scanner = new Scanner(inputFile);
		Scanner scanner1 = new Scanner(inputFile);

		int i = 0;
		// count the number of lines, so an array can be made
		while (scanner1.hasNextLine()) {
			scanner1.nextLine();
			i++;
		}

		// create a int[][] array to store values
		int[][] arr = new int[i][2];

		i = 0;
		// read values, first value in arr[i][0] and second value in arr[i][1]
		while (scanner.hasNextLine()) {
			arr[i][0] = scanner.nextInt();
			arr[i][1] = scanner.nextInt();
			i++;
		}
		return arr;
	}

	
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	//						     File Writer                                  //
	//------------------------------------------------------------------------//
	//------------------------------------------------------------------------//
	private static void writeFile(double[][] arr){
	        BufferedWriter writer = null;
	        try {
	            	        	
	            File logFile = new File("result.txt");

	            // This will output the full path where the file will be written to...
	            System.out.println(logFile.getCanonicalPath());

	            writer = new BufferedWriter(new FileWriter(logFile));
	            
	            for(int i =0;i<4;i++){
	            	for(int j=1;j<arr[0].length;j++){
	            		//String val=Arrays.deepToString(arr[i][j]);
	            		writer.write(arr[i][j]+"");
	            		writer.newLine();
	            	}
	            	writer.newLine();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	            }
	        }
	    
	}
}
