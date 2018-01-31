package conferences.msr2018.CrossRec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DataReader {

	
	
	public DataReader() {		
		
	}
	
	public Map<Integer, String> readRepositoryList(String filename){		
		Map<Integer,String> ret = new HashMap<Integer,String>();
		String[] vals = null;		
		String line="",uri="";
		int id=0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {				
				line = line.trim();
				vals = line.split("\t");
				id=Integer.parseInt(vals[0].trim());
				uri=vals[1].trim();
				ret.put(id,uri);							
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret;			
	}
	
	public Map<Integer, String> readProjectList(String filename, int startPos, int endPos){		
		Map<Integer,String> ret = new HashMap<Integer,String>();
		String[] vals = null;		
		String line="",repo="";
		int count=1;
		int id=startPos;		
		try {					
			BufferedReader reader = new BufferedReader(new FileReader(filename));			
			while (count< startPos) {
				line = reader.readLine();
				count++;
			}
			
			while (((line = reader.readLine()) != null)) {
				line = line.trim();							
//				vals = line.split("\t");					
//				repo=vals[1].trim();				
				repo=line;
				ret.put(id,repo);				
				id++;
				count++;
				if(count>endPos)break;
			}						
				
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return ret;				
	}
		
		
	
	public Map<String, Integer> readDictionary(String filename) {							
		Map<String, Integer> vector = new HashMap<String, Integer>();		
		String line = null;		
		String[] vals = null;		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				int ID = Integer.parseInt(vals[0].trim());				
				String URI = vals[1].trim();		
				vector.put(URI, ID);						
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		return vector;		
	}
		
	public Map<Integer, String> readDictionary2(String filename) {							
		Map<Integer, String> vector = new HashMap<Integer, String>();		
		String line = null;		
		String[] vals = null;		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
						
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				int ID = Integer.parseInt(vals[0].trim());				
				String URI = vals[1].trim();		
				vector.put(ID, URI);							
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		return vector;		
	}
		
	public Map<Integer, String> extractHalfDictionary(String filename, String groundTruthPath, boolean getAlsoUsers) {							
		Map<Integer, String> dict = new HashMap<Integer, String>();
		Map<Integer, String> ret = new HashMap<Integer, String>();
		String line = null;		
		String[] vals = null;
		int half=0;
		int libCount = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {				
				vals = line.split("\t");			
				int ID = Integer.parseInt(vals[0].trim());				
				String artifact = vals[1].trim();				
				dict.put(ID, artifact);
				if(artifact.contains("#DEP#"))libCount++;
			}
			
			reader.close();
			int size = libCount;
			
			Set<Integer> keySet = dict.keySet();			
			half = Math.round(size/2);			
								
			boolean enoughLib = false;
			libCount = 0;			
				
			int pos = filename.lastIndexOf("/");				
			String fname = filename.substring(pos+1,filename.length());		
			fname = fname.replace("dicth_", "");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(groundTruthPath + fname));
					
			/*Read a half of the dictionary and all users, the other half is put into the ground-truth data*/
			
			for(Integer key:keySet) {					
				String artifact = dict.get(key);
				if(libCount==half)enoughLib=true;							
				if(artifact.contains("#DEP#")) {					
					if(!enoughLib) {
						ret.put(key, artifact);
					}
					else {						
						String content = key + "\t" + artifact;					
						writer.append(content);							
						writer.newLine();
						writer.flush();						
					}
					libCount++;				
				} else {
					/*put users into the dictionary*/
					if(getAlsoUsers || artifact.contains("git://github.com/"))ret.put(key, artifact);
				}									
			}
			
			writer.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return ret;		
	}
		
	/*read only the first half of the libraries*/	
	public Set<String> getHalfOfLibraries(String filename) {							
		Map<Integer, String> dict = new HashMap<Integer, String>();
		Set<String> ret = new HashSet<String>();
		String line = null;		
		String[] vals = null;
		int libCount = 0, half=0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {				
				vals = line.split("\t");			
				int ID = Integer.parseInt(vals[0].trim());				
				String artifact = vals[1].trim();				
				dict.put(ID, artifact);			
				if(artifact.contains("#DEP#"))libCount++;
			}		
			reader.close();
			
			int size = libCount;
			Set<Integer> keySet = dict.keySet();			
			half = Math.round(size/2);		
			libCount=0;			
			for(Integer key:keySet) {					
				String artifact = dict.get(key);							
				if(artifact.contains("#DEP#")) {					
					ret.add(artifact);
					libCount++;				
				}
				if(libCount==half)break;
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return ret;		
	}
	
	
	/*get the list of libraries for one project*/		
	public Set<String> getLibraries(String filename) {							
		Set<String> vector = new HashSet<String>();		
		String line = null;		
		String[] vals = null;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {				
				vals = line.split("\t");								
				String library = vals[1].trim();
				if(library.contains("#DEP#"))vector.add(library);		
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return vector;		
	}
	
	
	
	/*read the whole file*/	
	public Map<Integer,String> readRecommendationFile(String filename) {							
		Map<Integer,String> ret = new HashMap<Integer,String>();	
		String line = null;		
		String[] vals = null;
		int id=1;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));					
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				String library = vals[0].trim();			
				ret.put(id, library);
				id++;
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		return ret;		
	}
	
	/*read a specific number of lines from the file*/	
	public Set<String> readRecommendationFile(String filename, int size) {							
		Set<String> ret = new HashSet<String>();	
		String line = null;		
		String[] vals = null;
		int count=0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));					
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				String library = vals[0].trim();					
				ret.add(library);
				count++;
				if(count==size)break;
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		return ret;		
	}
	
	public Set<String> readGroundTruthFile(String filename) {							
		Set<String> ret = new HashSet<String>();	
		String line = null;		
		String[] vals = null;		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));					
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				String library = vals[1].trim();					
				ret.add(library);								
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return ret;		
	}
		
	
	public Map<Integer, String> getMostSimilarProjects(String filename, int size) {							
		Map<Integer, String> projects = new HashMap<Integer, String>();		
		String line = null;		
		String[] vals = null;		
		int count=0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");							
				String URI = vals[1].trim();		
				projects.put(count, URI);
				count++;
				if(count==size)break;
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		return projects;		
	}
	

	
	/*Read dictionary, get only liraries and the first line*/	
	public Map<Integer, String> readDictionary4(String filename) {							
		Map<Integer, String> vector = new HashMap<Integer, String>();		
		String line = null;		
		String[] vals = null;		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				int ID = Integer.parseInt(vals[0].trim());				
				String artifact = vals[1].trim();				
				if(ID==1 || artifact.contains("#DEP#"))vector.put(ID, artifact);
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return vector;		
	}
		
	public Map<Integer, Double> getSimilarityMatrix(String filename, int size) {							
		Map<Integer, Double> sim = new HashMap<Integer, Double>();		
		String line = null;		
		String[] vals = null;		
		int count=0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));						
			while ((line = reader.readLine()) != null) {										
				vals = line.split("\t");			
				Double val = Double.parseDouble(vals[2].trim());
				sim.put(count, val);
				count++;
				if(count==size)break;
			}			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}						
		return sim;		
	}	
}