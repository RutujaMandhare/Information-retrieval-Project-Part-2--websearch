/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.tika.parser.microsoft.WordExtractor;


/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawlController {
	static int Count;

	public static void main(String[] args) throws Exception {

		String crawlStorageFolder = "C:\\madhuri\\crawler4jStorage";

		if (args.length != 2) {
			System.out.println("Needed parameters: ");
			System.out
					.println("\t rootFolder (it will contain intermediate crawl data)");
			System.out
					.println("\t numberOfCralwers (number of concurrent threads)");
			return;
		}

		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */

		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		int numberOfCrawlers = 1;

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Be polite: Make sure that we don't send more than 1 request per
		 * second (1000 milliseconds between requests).
		 */
		config.setPolitenessDelay(1000);

		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(-1);

		/*
		 * You can set the maximum number of pages to crawl. The default value
		 * is -1 for unlimited number of pages
		 */
		config.setMaxPagesToFetch(1000);

		/*
		 * Do you need to set a proxy? If so, you can use:
		 * config.setProxyHost("proxyserver.example.com");
		 * config.setProxyPort(8080);
		 * 
		 * If your proxy also needs authentication:
		 * config.setProxyUsername(username); config.getProxyPassword(password);
		 */

		/*
		 * This config parameter can be used to set your crawl to be resumable
		 * (meaning that you can resume the crawl from a previously
		 * interrupted/crashed crawl). Note: if you enable resuming feature and
		 * want to start a fresh crawl, you need to delete the contents of
		 * rootFolder manually.
		 */
		config.setResumableCrawling(false);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		BasicCrawler basicCrawler = new BasicCrawler();

		/*
		 * Fetching robots.txt for that particular url
		 */
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher,
				robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		/*
                *  
                */
		controller.addSeed("http://lyle.smu.edu/~fmoore/");
		// controller.addSeed("http://lyle.smu.edu/~fmoore/");
		// controller.addSeed("http://lyle.smu.edu/~fmoore/");

		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(BasicCrawler.class, numberOfCrawlers);

		List<String> dict_list = new ArrayList<String>(BasicCrawler.dict);
		List<String> words_list = new ArrayList<String>();
		List<String> doc_list = new ArrayList<String>();
		// Term Freq Matrix
		int[][] dict_term_matrix = new int[dict_list.size()][BasicCrawler.docid + 1];
		// First initializing complete matrix to 0s
		for (int a = 0; a < dict_list.size(); a++) {
			for (int b = 0; b < BasicCrawler.docid + 1; b++) {
				dict_term_matrix[a][b] = 0;
			}
		}
		// For loop to get files according to docid
		for (int i = 1; i <= BasicCrawler.docid; i++) {
			// list which will have words from file
			File f = new File(
					"C:/sharath_SMU/Spring_work/logs/"+ i + ".log");
			// to check if file exists or not
			if (f.exists()) {

				try (BufferedReader reader = new BufferedReader(new FileReader(
						f))) {
					String line = null;
					// logic to load words into list
					while ((line = reader.readLine()) != null) {
						words_list.add(line);
						doc_list.add(line);
					}
				} catch (IOException x) {
					System.err.format("IOException: %s%n", x);
				}
				// for loop for dict size
				for (int j = 0; j < dict_list.size(); j++) {
					// for loop for document words list
					for (int k = 0; k < words_list.size(); k++) {
						// comparison
						if (dict_list.get(j).equals(words_list.get(k))) {
							// this counts term frequency according to word in
							// dictionary by using how many terms are there in
							// document
							Count = Collections.frequency(words_list,
									dict_list.get(j));
							// put term frequency in matrix
							dict_term_matrix[j][i] = Count;
						}
					}
				}
				words_list.clear();
			}

			else {
				continue;
			}
		}

		// for(int i=1;i <=BasicCrawler.docid;i++){
		// System.out.println("Doc"+i);
		// for(int j=0; j < dict_list.size();j++ ){
		//
		// System.out.println(dict_list.get(i)+" " +dict_term_matrix[j][i]+" ");
		// }
		// System.out.println();
		// }
		 List<Integer> doc_array = new ArrayList<Integer>();
		  List<Integer> query_array = new ArrayList<Integer>();
		 
		  repeat:
		  while(true){
			  int validcount=0;
			  double temp;
			  double qtemp;
			  double dtemp;
			  double idf;
			  double tf_idf;
			  double qidf;
			  double qsquaresum=0;
			  List<String>query_list=new ArrayList<String>();
			  double[][] tfidf_list=new double[dict_list.size()][BasicCrawler.docid+1];
			  double cosine=0;
			  double cotemp=0;
			  List<Double> cosinesim=new ArrayList<Double>();
			  List<Double> dlist = new ArrayList<Double>();
			  List<Double> qsquare = new ArrayList<Double>();
			  List<Double> crosstemp2=new ArrayList<Double>();
			  List<Double> qdlist=new ArrayList<Double>();
			  List<Double> qtf_idf_list=new ArrayList<Double>();
			  List<Double> qidf_list=new ArrayList<Double>();
			  List <Double> idf_list=new ArrayList<Double>();
			  
		  System.out.println("Enter our query here. When you want to stop searching please press STOP");
		  BufferedReader qr=new BufferedReader(new InputStreamReader(System.in));
		  String query=qr.readLine();
		if(query.equalsIgnoreCase("STOP"))
		{
			System.out.println("Program Terminated");
			break;
		}
			query_list.clear();
		    String qwords[] = query.split(" ");
			for(int i=0;i<qwords.length;i++)
			{
				query_list.add(qwords[i].toLowerCase());	
			}
			
			for(int i=0; i<query_list.size();i++){
				validcount=Collections.frequency(dict_list, query_list.get(i));
				if(validcount==0){
					System.out.println(query_list.get(i)+"  is not in dictinary. please enter other term");
					continue repeat;
				}
			}
			
			for(int i=0;i<query_list.size();i++){
				int query_count=Collections.frequency(query_list, query_list.get(i));
						query_array.add(query_count);
				         qtemp=BasicCrawler.docid/query_count;
//				         System.out.println("queryCount  "+query_count);
				        qidf=1+(Math.log10(qtemp));
						qidf_list.add(qidf);
						
			}
			for(int i=0;i<query_list.size();i++)
			{
						qtf_idf_list.add(query_array.get(i)*qidf_list.get(i));
				        qsquare.add(Math.pow(qtf_idf_list.get(i), 2));
				        qsquaresum=qsquaresum+qsquare.get(i); 
				        }
			qsquaresum=Math.sqrt(qsquaresum);
//			System.out.println("qsquaresum  "+qsquaresum);
			for(int i=0;i<dict_list.size();i++){
				int doc_count=Collections.frequency(doc_list, dict_list.get(i));
				temp=BasicCrawler.docid/doc_count;
				idf=1+(Math.log10(temp));
				//System.out.println("idf  "+idf);
				idf_list.add(idf);
				doc_array.add(doc_count);
			}  
			 for(int j=0;j<dict_list.size();j++){
				   for(int k=0;k<BasicCrawler.docid+1;k++){
					   tfidf_list[j][k]=0;
				   }
			  } 
			 double[][] match_tf_list=new double[query_list.size()][BasicCrawler.docid+1];
			for(int j=0;j<query_list.size();j++){
				   for(int k= 0;k<BasicCrawler.docid+1;k++){
					   match_tf_list[j][k]=0;
				   }
			  }

		      	for(int k=0, j=0;k<idf_list.size() && j< dict_list.size();j++){
		      		for(int i= 1;i<=BasicCrawler.docid;i++){
		      			tf_idf=dict_term_matrix[j][i]*idf_list.get(k);
		      			tfidf_list[j][i]=tf_idf;
//		      			System.out.println("tfidf  "+tfidf_list[j][i]);
		      		}
		      }
		        for(int i=0;i<query_list.size();i++){
		     	   for(int j=0;j<dict_list.size();j++){
		     		   for(int k=1;k<=BasicCrawler.docid;k++){
		     			   if(query_list.get(i).equals(dict_list.get(j))){
		     				   match_tf_list[i][k]=tfidf_list[j][k];
		     			   }
		     			   
		     		   }
		     	   }
		        }
		    	 
		    	   for(int k=1; k<=BasicCrawler.docid;k++){
		    		   double dc=0;
		    		   for(int i=0;i<query_list.size();i++){
		        		 dtemp=Math.pow(match_tf_list[i][k],2);
		        		 dc=dc+dtemp;
		        		 if(i==query_list.size()-1){
//		        			 System.out.println("DC  "+dc);
		        			 dlist.add(Math.sqrt(dc));
		        	
		         }
		         
		         }
		  }
		       
		 
		   double[][] crosstemp= new double[query_list.size()][BasicCrawler.docid+1];
		   double crosstemp1=0;
		  for(int i = 0,j=0;i<query_list.size() && j<dict_list.size();i++,j++){
		       for(int k=1;k<=BasicCrawler.docid;k++){
		           crosstemp[j][k]=qtf_idf_list.get(i)*match_tf_list[j][k];        	
		       }  
		  }
		  for(int j=1;j<=BasicCrawler.docid;j++){
			  for(int k=0;k<query_list.size();k++){
				  crosstemp1=crosstemp1+crosstemp[k][j];
			  }
			  crosstemp2.add(crosstemp1);
				  
		  }
		  for(int i=0;i<dlist.size();i++){
			  cotemp=qsquaresum*dlist.get(i);
//			  System.out.println("COTEMP  "+cotemp);
		      qdlist.add(cotemp);
		}
		  for(int i=0,j=0;i<qdlist.size() && j<crosstemp2.size();i++,j++)
		  {
			  cosine=crosstemp2.get(j)/qdlist.get(i);
			  List<String> top20_list = new ArrayList<String>();
		  if(cosine>0 && cosine!=Double.POSITIVE_INFINITY){
			  int later_i = i+1;
			  System.out.println("Cosine Similarity:  "+cosine+ " & value of i:  " +later_i);
					System.out.println("URL: "
							+ BasicCrawler.url_list.get(later_i));
					File f = new File(
							"C:/sharath_SMU/Spring_work/logs/"+ later_i + ".log");
					// to check if file exists or not
					if (f.exists()) {

						try (BufferedReader reader = new BufferedReader(
								new FileReader(f))) {
							String line = null;
							// logic to load words into list
							while ((line = reader.readLine()) != null) {
								top20_list.add(line);

							}
						} catch (IOException x) {
							System.err.format("IOException: %s%n", x);
						}
						System.out.println(top20_list.size());
						if (top20_list.size() > 20) {
							for (int d = 0; d < 21; d++) {
								if (!top20_list.get(d).isEmpty())
									System.out.println(top20_list.get(d));

							}
						} else {
							for (int e = 0; e < top20_list.size(); e++) {
								if (!top20_list.get(e).isEmpty())
									System.out.println(top20_list.get(e));

							}
						}
					}
				}

			}
		}
	}

}