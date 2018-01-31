package conferences.msr2018.CrossRec;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Sets;


class ValueComparator implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}



public class Runner {
	
	private String srcDir;	
	private String simDir;
	private String subFolder;
		
	public Runner(){
		
	}
	
	public void loadConfigurations(){		
		Properties prop = new Properties();				
		try {
			prop.load(new FileInputStream("evaluation.properties"));		
			this.srcDir=prop.getProperty("sourceDirectory");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
			
	
	public void run(){		
		System.out.println("CrossRec: A Collaborative-Filtering Recommender System");
		loadConfigurations();
				
		/*refer to ListOfFolds.txt for a complete list*/
		
		int trainingStartPos1 = 1;
		int trainingEndPos1 = 1080;
		int trainingStartPos2 = 0;
		int trainingEndPos2 = 0;
				
		int testingStartPos = 1081;
		int testingEndPos = 1200;
		subFolder = "Round1";
		
		//subFolder = "Round10";
		this.simDir = this.srcDir + subFolder + "/";
		
		SimilarityCalculator calculator = new SimilarityCalculator(this.srcDir,this.subFolder,
				trainingStartPos1,
				trainingEndPos1,
				trainingStartPos2,
				trainingEndPos2,
				testingStartPos,
				testingEndPos);

		calculator.ComputeWeightCosineSimilarity();
							
		RecommendationEngine engine = new RecommendationEngine(this.srcDir,this.subFolder, testingStartPos, testingEndPos);
		
	    int numberOfNeighbours = 20;
	    
		engine.UserBasedRecommendation(numberOfNeighbours);			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*get the similarity matrix from the original RepoPal dataset*/
	
	public void ExtractingRepoPalSimilarityMatrix(int testingStartPos, int testingEndPos){
		
		DataReader reader = new DataReader();
			
		Map<Integer,String> testingProjects = new HashMap<Integer,String>();
		testingProjects = reader.readProjectList(this.srcDir + "projects.txt",testingStartPos,testingEndPos);
			
		Set<Integer> keyTestingProjects = testingProjects.keySet();
						
		String testingPro = "";
		
		Set<String> tests = new HashSet<String>();
			
		
		for(Integer keyTesting:keyTestingProjects){
			System.out.println(testingProjects.get(keyTesting));
			tests.add(testingProjects.get(keyTesting));
		}
		
		System.out.println("size: " + tests.size());
		
//		Set<String> values = (Set<String>) testingProjects.values();
		
		for(Integer keyTesting:keyTestingProjects){
			
			testingPro = testingProjects.get(keyTesting);		
			String filename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");		
			String tmp = this.simDir + filename;					
			/*This is only for RepoPal*/
			
			tmp = tmp.replace("__", "_");
			
			String tmp2 = this.simDir + "Similarities" + "/" + filename;						
			String line = null;				
					
			String[] vals = null;
			
			try {
				BufferedReader bufread = new BufferedReader(new FileReader(tmp));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmp2));				
				while ((line = bufread.readLine()) != null) {					
					vals = line.split("\t");							
					String secondProj = vals[1].trim().replace("git://github.com/", "").replace(".git", "");
								
					if(!tests.contains(secondProj)) {										
						writer.append(line);							
						writer.newLine();
						writer.flush();					
					}				
				}
				writer.close();
				bufread.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
	}
		
		
	
	
	
	public static void main(String[] args) {	
		Runner runner = new Runner();			
		runner.run();				    		    
		return;
	}	
	
}
