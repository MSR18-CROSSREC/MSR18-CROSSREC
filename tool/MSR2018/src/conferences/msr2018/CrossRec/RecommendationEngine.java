package conferences.msr2018.CrossRec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class RecommendationEngine {
	
	private String srcDir;
	private String simDir;
	private String recDir;
	private String subFolder;
	
	private int testingStartPos;
	private int testingEndPos;
		
	public RecommendationEngine(String sourceDir, String suFolder, int teStartPos, int teEndPos) {			
		this.srcDir = sourceDir;
		this.subFolder = suFolder;		
		this.recDir = this.srcDir + subFolder + "/" + "Recommendations" + "/";
		this.simDir = this.srcDir + subFolder + "/";		
		this.testingStartPos = teStartPos;
		this.testingEndPos = teEndPos;	
	}
		
		
	/*Recommends libraries to test projects using the user-based collaborative-filtering techniques*/
	
	public void UserBasedRecommendation(int numberOfNeighbours){
				
		DataReader reader = new DataReader();			
		Map<Integer,String> testingProjects = new HashMap<Integer,String>();
		testingProjects = reader.readProjectList(this.srcDir + "projects.txt",testingStartPos,testingEndPos);
						
		Set<Integer> keyTestingProjects = testingProjects.keySet();
						
		String testingPro = "", testingFilename = "";
			
		Set<String> testingLibs = null;
				
		String filename="",testingDictFilename="";
		
		Set<Integer> keySet = null;
												
		for(Integer keyTesting:keyTestingProjects){
		
			Map<Integer, Set<String>> allNeighbourLibs = new HashMap<Integer, Set<String>>();
			Set<String> libraries = new HashSet<String>();
			Set<String> libs = new HashSet<String>();
			List<String> libSet = new ArrayList<String>();			
			Map<Integer, String> simProjects = new HashMap<Integer, String>();				
						
			testingPro = testingProjects.get(keyTesting);
			filename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");			
			testingFilename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");						
			testingDictFilename = this.srcDir +"dicth_" + testingFilename;		
			testingLibs = new HashSet<String>();			
			testingLibs = reader.getHalfOfLibraries(testingDictFilename);
					
						
			Map<String, Double> recommendations = new HashMap<String, Double>();
			Map<Integer, Double> simMatrix = new HashMap<Integer, Double>();
						
			String tmp = this.simDir + filename;			
					
			simProjects = reader.getMostSimilarProjects(tmp, numberOfNeighbours);
			simMatrix = reader.getSimilarityMatrix(tmp, numberOfNeighbours);
			keySet = simProjects.keySet();
					
															
			for(Integer key:keySet) {				
				String project = simProjects.get(key);												
				filename = project.replace("git://github.com/", "").replace(".git", "").replace("/", "__");			
				tmp = this.srcDir + "dicth_" + filename;				
				libs = reader.getLibraries(tmp);				
				allNeighbourLibs.put(key, libs);
				libraries.addAll(libs);				
			}
					
			allNeighbourLibs.put(numberOfNeighbours, testingLibs);
						
			/*The list of all libraries from the training projects and the testing project*/
			libraries.addAll(testingLibs);
			/*change the set to an ordered list*/
			for(String l:libraries)libSet.add(l);
						
			/*Number of projects, including the test project*/
			int M = numberOfNeighbours + 1;
			/*Number of libraries*/
			int N = libraries.size();
						
			double UserItemMatrix[][] = new double[M][N];
			
			/*assign the user-item matrix*/
			for(int i=0;i<numberOfNeighbours;i++) {				
				Set<String> tmpLibs = allNeighbourLibs.get(i);								
				for(int j=0;j<N;j++) {					
					if(tmpLibs.contains(libSet.get(j))) {											
						UserItemMatrix[i][j]=1.0;						
					}
					else UserItemMatrix[i][j]=0;				
				}
			}					
						
			/*Here is the test project and it needs recommendation. It is located at the end of the list.*/
			
			Set<String> tmpLibs = allNeighbourLibs.get(numberOfNeighbours);				
			for(int j=0;j<N;j++) {
				String str = libSet.get(j);
				if(tmpLibs.contains(str))UserItemMatrix[numberOfNeighbours][j]=1.0;
				else {
					UserItemMatrix[numberOfNeighbours][j]=-1.0;					
				}
			}
						
			/*calculate the missing ratings using item-based collaborative-filtering recommendation*/						
			double tmpUserItemMatrix[][] = new double[M][N];
			/*copy the matrix*/			
			for(int i=0;i<M;i++) {
				for(int j=0;j<N;j++) {
					tmpUserItemMatrix[i][j]=UserItemMatrix[i][j];					
				}				
			}
						
			double val1 = 0;		
			/*average rating is computed for the projects that include a library in the library set, so it is 1*/
			double avgRating = 1.0, tmpRating = 0.0;

			for(int k=0;k<numberOfNeighbours;k++)val1+=simMatrix.get(k);
					
			for(int j=0;j<N;j++) {				
				if(UserItemMatrix[numberOfNeighbours][j]==-1) {					
					double val2=0;					
					for(int k=0;k<numberOfNeighbours;k++) {
						tmpRating = 0;
						for(int l=0;l<N;l++)tmpRating+=UserItemMatrix[k][l];
						tmpRating = (double)tmpRating/N;						
						val2+=(UserItemMatrix[k][j]-tmpRating)*simMatrix.get(k);									
					}					
					UserItemMatrix[numberOfNeighbours]														[j] = avgRating + val2/val1;				
					recommendations.put(Integer.toString(j), UserItemMatrix[numberOfNeighbours][j]);				
				}				
			}								
						
			ValueComparator bvc =  new ValueComparator(recommendations);        
			TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
			sorted_map.putAll(recommendations);				
			Set<String> keySet2 = sorted_map.keySet();				
		
			filename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");
			
			try {
				tmp = this.recDir + filename;
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));												
				for(String key:keySet2){					
					String content = libSet.get(Integer.parseInt(key)) + "\t" + recommendations.get(key);					
					writer.append(content);							
					writer.newLine();
					writer.flush();					
				}			
			writer.close();							
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
		return;
	}
		
	
	/*Recommends libraries to test projects using the item-based collaborative-filtering technique*/
	
	public void ItemBasedRecommendation(int numberOfNeighbours){
				
		DataReader reader = new DataReader();			

		Map<Integer,String> testingProjects = new HashMap<Integer,String>();
		testingProjects = reader.readProjectList(this.srcDir + "projects.txt",testingStartPos,testingEndPos);
		Set<String> testingLibs = null;
				
		Set<Integer> keyTestingProjects = testingProjects.keySet();
						
		String testingPro = "";
		String testingFilename = "";
			
		String filename="";
		String testingDictFilename="";
		Set<Integer> keySet = null;
											
		for(Integer keyTesting:keyTestingProjects){
		
			Map<Integer, Set<String>> allNeighbourLibs = new HashMap<Integer, Set<String>>();
			Set<String> libraries = new HashSet<String>();
			Set<String> libs = new HashSet<String>();
			List<String> libSet = new ArrayList<String>();			
			Map<Integer, String> simProjects = new HashMap<Integer, String>();	
			testingPro = testingProjects.get(keyTesting);
			filename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");			
			testingFilename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");			
			testingDictFilename = this.srcDir +"dicth_" + testingFilename;		
			testingLibs = new HashSet<String>();			
			testingLibs = reader.getHalfOfLibraries(testingDictFilename);
															
			Map<String, Double> recommendations = new HashMap<String, Double>();
			String tmp = this.simDir + filename;			
			
			simProjects = reader.getMostSimilarProjects(tmp, numberOfNeighbours);									
			keySet = simProjects.keySet();
															
			for(Integer key:keySet) {				
				String project = simProjects.get(key);												
				filename = project.replace("git://github.com/", "").replace(".git", "").replace("/", "__");			
				tmp = this.srcDir + "dicth_" + filename;				
				libs = reader.getLibraries(tmp);				
				allNeighbourLibs.put(key, libs);
				libraries.addAll(libs);				
			}
					
			allNeighbourLibs.put(numberOfNeighbours, testingLibs);
						
			/*The list of all libraries from the training projects and the testing project*/
			libraries.addAll(testingLibs);
			/*change the set to an ordered list*/
			for(String l:libraries)libSet.add(l);
						
			/*Number of projects, including the test project*/
			int M = numberOfNeighbours + 1;
			/*Number of libraries*/
			int N = libraries.size();
						
			double UserItemMatrix[][] = new double[M][N];
			
			/*assign the user-item matrix*/
			for(int i=0;i<numberOfNeighbours;i++) {				
				Set<String> tmpLibs = allNeighbourLibs.get(i);								
				for(int j=0;j<N;j++) {					
					if(tmpLibs.contains(libSet.get(j))) {												
						UserItemMatrix[i][j]=1.0;						
					}
					else UserItemMatrix[i][j]=0;				
				}
			}
								
			/*Here is the test project and it needs recommendation. It is located at the end of the list.*/			
			Set<String> tmpLibs = allNeighbourLibs.get(numberOfNeighbours);				
			for(int j=0;j<N;j++) {
				String str = libSet.get(j);
				if(tmpLibs.contains(str))UserItemMatrix[numberOfNeighbours][j]=1.0;
				else {
					UserItemMatrix[numberOfNeighbours][j]=-1.0;					
				}
			}
						
			/*calculate the missing ratings using item-based collaborative-filtering recommendation*/						
			double tmpUserItemMatrix[][] = new double[M][N];
			/*copy the matrix*/			
			for(int i=0;i<M;i++) {
				for(int j=0;j<N;j++) {
					tmpUserItemMatrix[i][j]=UserItemMatrix[i][j];					
				}				
			}
						
			double vector1[] = new double[numberOfNeighbours];
			double vector2[] = new double[numberOfNeighbours];					
			double sim = 0,tmp1=0,tmp2=0;					
			double avgRating = 0.1, tmpRating = 0.0;
								
			for(int j=0;j<N;j++) {									
				tmp1=0;
				tmp2=0;
				if(UserItemMatrix[numberOfNeighbours][j]==-1) {	
					tmpRating = 0.0;			
					for(int l=0;l<numberOfNeighbours;l++) {
						tmpRating+=tmpUserItemMatrix[l][j];
					}					
					tmpRating = (double)tmpRating/numberOfNeighbours;					
					for(int k=0;k<N;k++) {						
						if((k!=j)&&UserItemMatrix[numberOfNeighbours][k]!=-1) {						
							for(int l=0;l<numberOfNeighbours;l++) {
								vector1[l]= tmpUserItemMatrix[l][k];
								vector2[l]= tmpUserItemMatrix[l][j];							
							}						
							sim=CosineSimilarity(vector1,vector2);
							tmp1+=sim*(UserItemMatrix[numberOfNeighbours][k]-avgRating);
							tmp2+=sim;						
						}				
					}													
					double val = 0;
					if(tmp1!=0 && tmp2!=0)val=(double)tmp1/tmp2;
					else val = 0;
					
					val+=tmpRating;							
					UserItemMatrix[numberOfNeighbours][j]=val;				
					recommendations.put(Integer.toString(j), UserItemMatrix[numberOfNeighbours][j]);								
				}						
			}						
					
			ValueComparator bvc =  new ValueComparator(recommendations);        
			TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
			sorted_map.putAll(recommendations);				
			Set<String> keySet2 = sorted_map.keySet();				
		
			filename = testingPro.replace("git://github.com/", "").replace(".git", "").replace("/", "__");
			
			try {
				tmp = this.recDir + filename;
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));												
				for(String key:keySet2){					
					String content = libSet.get(Integer.parseInt(key)) + "\t" + recommendations.get(key);					
					writer.append(content);							
					writer.newLine();
					writer.flush();					
				}			
			writer.close();							
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
		return;
	}
		
	
	public double CosineSimilarity(double[] vector1, double[] vector2) {        
        double sclar = 0, norm1 = 0, norm2 = 0;
        int length = vector1.length;       
        for(int i=0;i<length;i++) sclar+=vector1[i]*vector2[i];
        for(int i=0;i<length;i++) norm1+=vector1[i]*vector1[i];
        for(int i=0;i<length;i++) norm2+=vector2[i]*vector2[i];
        double ret = 0;
        double norm = norm1*norm2;                
        if(norm>0 && sclar>0) ret = (double)sclar / Math.sqrt(norm);        
        else ret =0;
        return ret;
    }

}
