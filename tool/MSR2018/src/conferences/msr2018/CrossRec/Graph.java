package conferences.msr2018.CrossRec;

import java.io.*;
import java.util.*;

public class Graph {

	/**
	 *  A Map storing InLinks. For each identifier (the key), another Map is stored,
	 *  containing for each inlink an associated "connection weight"
	 */
	private Map<Integer,Set<Integer>> OutLinks;
	
	
	/** The number of nodes in the graph */
	private int nodeCount;
	
	BufferedWriter writer = null;	

	/**
	 * Constructor for Graph
     *
	 */
	
	Map<String, Integer> dictionary = new HashMap<String, Integer>();
		
	public Graph () {		
		OutLinks = new HashMap<Integer,Set<Integer>>();
		nodeCount = 0;
	}
	
	public Map<Integer,Set<Integer>> getOutLinks(){
		return OutLinks;
	}
	
	public void setOutLinks(Map<Integer,Set<Integer>> OutLinks){
		this.OutLinks = OutLinks;
	}
	
	public void setNumNodes(int n){
		this.nodeCount = n;
	}
	
	public int getNumNodes(){
		return this.nodeCount;
	}
	
	public Map<String, Integer> getDictionary(){
		return this.dictionary;
	}
	
	
	public Graph (String fileName) {
		
		OutLinks = new HashMap<Integer,Set<Integer>>();
		nodeCount = 0;
		
		BufferedReader reader = null;
		String line;
		String[] vals = null;
		String[] pair = null;
		
		Set<Integer> outlinks = new HashSet<Integer>();
		Set<Integer> nodes = new HashSet<Integer>();
		
		File file = new File(fileName);
				
		try {
			reader = new BufferedReader(new FileReader(file));			
			while((line=reader.readLine())!=null) {				
				vals = line.split(",");					
				for(String pairs:vals){
					pair = pairs.split("#");				
					Integer startNode = Integer.parseInt(pair[0].trim());
					Integer endNode = Integer.parseInt(pair[1].trim());					
					nodes.add(startNode);
					nodes.add(endNode);														
					if(OutLinks.containsKey(startNode)){				
						outlinks = OutLinks.get(startNode);											
					} else {
						outlinks = new HashSet<Integer>();						
					}
					outlinks.add(endNode);
					OutLinks.put(startNode, outlinks);
				}						
			}
			reader.close();
			nodeCount=nodes.size();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Graph (Graph graph) {		
		this.OutLinks = new HashMap<Integer, Set<Integer>>();
		this.dictionary = new HashMap<String, Integer>();
		this.nodeCount = 0;
				
		Set<Integer> thisInlinks = new HashSet<Integer>();				
		Map<Integer, Set<Integer>> OutLinks = graph.getOutLinks();		
		Set<Integer> keySet = OutLinks.keySet();
		
		/*Copy the graph*/
		
		for(Integer endNode:keySet) {		
			thisInlinks = new HashSet<Integer>();
			Set<Integer> inlinks = OutLinks.get(endNode);
			for(Integer startNode:inlinks)thisInlinks.add(startNode);
			this.OutLinks.put(endNode, thisInlinks);		
		}
		
		Map<String, Integer> dictionary = graph.getDictionary();
		Set<String> dictKeySet = dictionary.keySet();
				
		/*Copy the dictionary*/
		for(String key:dictKeySet) {
			Integer val = dictionary.get(key);
			this.dictionary.put(key, val);
		}
		
		/*Set the number of nodes*/
		this.setNumNodes(graph.getNumNodes());	
	}

	
	public Graph (String fileName, Map<Integer, String> Dictionary) {		
		OutLinks = new HashMap<Integer, Set<Integer>>();
		nodeCount = 0;		
		BufferedReader reader = null;
		String line;		
		String[] pair = null;		
		Set<Integer> outlinks = new HashSet<Integer>();
		Set<Integer> nodes = new HashSet<Integer>();		
		Set<Integer> keySet = Dictionary.keySet();
			
		File file = new File(fileName);
				
		try {
			reader = new BufferedReader(new FileReader(file));			
			while((line=reader.readLine())!=null) {											
				pair = line.trim().split("#");			
				
				/*WARNING: Only for temporary use, startNode must be pair[0] and endNode must be pair[1]*/
				
				int startNode = Integer.parseInt(pair[0].trim());
				int endNode = Integer.parseInt(pair[1].trim());				
								
				if(keySet.contains(startNode) && keySet.contains(endNode)) {				
					nodes.add(startNode);
					nodes.add(endNode);	
					
					if(OutLinks.containsKey(startNode)){				
						outlinks = OutLinks.get(startNode);											
					} else {
						outlinks = new HashSet<Integer>();						
					}
					outlinks.add(endNode);
					OutLinks.put(startNode, outlinks);
				}										
			}
			reader.close();
			nodeCount=nodes.size();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void combine(Graph graph, Map<Integer, String> dictionary){	
		Map<Integer,Set<Integer>> tmpOutLinks = graph.getOutLinks();				
		Set<Integer> outlinks = new HashSet<Integer>();		
		Set<Integer> mainOutlinks = new HashSet<Integer>();		
		Set<Integer> key = tmpOutLinks.keySet();		
				
		String artifact = "";
		int idEndNode = 0, idStartNode = 0;		
					
		for(Integer startNode:key){				
			outlinks = tmpOutLinks.get(startNode);
			artifact = dictionary.get(startNode);				
			idStartNode = extractKey(artifact);
						
			for(Integer endNode:outlinks){							
				artifact = dictionary.get(endNode);
				idEndNode = extractKey(artifact);																					
				if(this.OutLinks.containsKey(idStartNode)){					
					mainOutlinks = this.OutLinks.get(idStartNode);					
				} else {				
					mainOutlinks = new HashSet<Integer>();					
				}			
				mainOutlinks.add(idEndNode);
				this.OutLinks.put(idStartNode, mainOutlinks);
			}			
		}		
		Set<Integer> nodes = new HashSet<Integer>();		
		key = this.OutLinks.keySet();	
		
		for(Integer startNode:key){	
			nodes.add(startNode);
			mainOutlinks = this.OutLinks.get(startNode);			
			for(Integer endNode:mainOutlinks){	
				nodes.add(endNode);
			}		
		}								
		nodeCount = nodes.size();		
	}
				
	private int extractKey(String s) {		
		if (dictionary.containsKey(s))
			return dictionary.get(s);
		else {
			int c = dictionary.size();			
			dictionary.put(s,c);						
			return c;
		}		
	}
	
	
	public Set<Integer> outLinks(int id){		
		return OutLinks.get(id);		
	}
	
	
	public int numNodes() {
		return nodeCount;
	}

}
