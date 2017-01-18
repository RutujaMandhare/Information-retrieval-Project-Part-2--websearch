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


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.sleepycat.je.Environment;



/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawler extends WebCrawler {
	
	static Map<Integer,String> url_list = new HashMap<Integer,String>();
	int counter=0;
	static int docid;
	static TreeSet<String> dict = new TreeSet<String>();  

  private static final Pattern BINARY_FILES_EXTENSIONS =
      Pattern.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps" +
                      "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox" +
                      "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv" +
                      "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha)" +
                      "(\\?.*)?$"); // For url Query parts ( URL?q=... )

  /**
   * You should implement this function to specify whether the given url
   * should be crawled or not (based on your crawling logic).
   */
  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    if(href.contains(".jpg")&& href.startsWith("http://lyle.smu.edu/~fmoore/"))
    {
    	counter++;
    	logger.debug("URL:{}",href);
    	logger.debug("Number of JPG's:{} " ,counter);
    }
    return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://lyle.smu.edu/~fmoore/");
 
  }

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */
  @Override
  public void visit(Page page) {
	docid = page.getWebURL().getDocid();//Document id is generated and set  for all the pages which are retrieved from the URL
    String url = page.getWebURL().getURL();
    int parentDocid = page.getWebURL().getParentDocid();
    
    //adding docid and url to map
    url_list.put(docid,url);//Lists all the URL which the crawler is visiting
    String domain = page.getWebURL().getDomain();
    String path = page.getWebURL().getPath();
    String subDomain = page.getWebURL().getSubDomain();
    String parentUrl = page.getWebURL().getParentUrl();
    String anchor = page.getWebURL().getAnchor();
    long millis = System.currentTimeMillis() % 1000;
    logger.debug("Docid: {}", docid); // Prints the Doc Id to the log
    logger.debug("URL: {}", url);  // Prints the list of URL
    logger.debug("Domain: '{}'", domain);
    logger.debug("Sub-domain: '{}'", subDomain);
    logger.debug("Path: '{}'", path);
    logger.debug("Parent page: {}", parentUrl);
    logger.debug("Anchor text: {}", anchor);
    
    

    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();// We use getter and setter methods to set and get the text from HTMLParseData.
      String html = htmlParseData.getHtml();
      /*The below set contains a set of out going links from the current URL*/
      Set<WebURL> links = htmlParseData.getOutgoingUrls();

      logger.debug("Text length: {}", text.length());
 
      if(text.length()>0){
			  if(url.contains("txt") || url.contains("htm") || url.contains("html")){
				
				  String words[] = text.split("[\\s\\t\\r.,():?\\//@~-]+");
//			  try {
//				System.setOut(new PrintStream(new FileOutputStream("C:/sharath_SMU/Spring_work/logs/"+docid+".log")));
//			} catch (FileNotFoundException e) {
//					e.printStackTrace();
//			}
				  for(int i=0;i < words.length;i++){
                	  
           		   if(words[i].substring(0, 1).matches("[a-zA-Z]+")){
           			   
           		  //System.out.println(words[i].toLowerCase());
           			   	dict.add(words[i].toLowerCase()); 
					 }
				 
				 }
      }    }
      logger.debug("Html length: {}", html.length());
      /* Prints the number of out going links fom the present URL and the out going links to the log*/
      logger.debug("Number of outgoing links: {}", links.size()); 
      logger.debug("Out Going URl links from the current URL: {}",links);
      
    }

    Header[] responseHeaders = page.getFetchResponseHeaders();
    if (responseHeaders != null) {
      logger.debug("Response headers:");
      for (Header header : responseHeaders) {
        logger.debug("\t{}: {}", header.getName(), header.getValue());
      }
    }
    
    logger.debug("=============");
  }
  
}
