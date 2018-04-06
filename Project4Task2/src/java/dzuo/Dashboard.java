/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dzuo;

/**
 *
 * @author zuodo
 */
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;

@WebServlet(name = "Dashboard",urlPatterns = {"/dashboard"})
public class Dashboard extends HttpServlet {
    // MongoDB  collection to use
    MongoCollection<Document> collection;
    // Initiate the database connection
    @Override
    public void init() {
        //connect to mongoDB database
        MongoClientURI connectionString = new MongoClientURI("mongodb://mongouser:mongopass@ds123399.mlab.com:23399/project4mongo");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("project4mongo");
        collection = database.getCollection("books");
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        //we only have web browser interface , so the doctype is for desktop device
        request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        //this list is used to display logs
        List<Log> logs=new ArrayList<>();
        //this four map is used to count how many accesses to a certain searh term, book title and book author.
        //these four maps will be sorted later and used to display the top 3 term, book title and book author.
        Map<String,Integer> searchMap=new HashMap<>();
        Map<String,Integer> titleMap=new HashMap<>();
        Map<String,Integer> authorMap=new HashMap<>();
        Map<String,Integer> deviceMap=new HashMap<>();
        //get all the logs and add to the list
        //at the same time update the map to get the analytics statistics
        try (MongoCursor<Document> cur =collection.find().iterator()) {
            while (cur.hasNext()) {
                Document doc = cur.next();
                String [] record=new String[8];
                int j=-1;
                for( String key :doc.keySet()){
                    if(!key.equals("_id")){
                        record[j]=(String)doc.get(key);
                    }else{
                        j++;
                        continue;
                    }
                    
                    String newkey=(String)doc.get(key);
                    if(key.equals("Log3  user entered search term:")){
                        if(!searchMap.containsKey(newkey)){
                            searchMap.put(newkey, 1);
                        }else{
                            searchMap.put(newkey, searchMap.get(newkey)+1);
                        }
                    }
                    if(key.equals("Log6  Found book with title:")){
                        if(!titleMap.containsKey(newkey)){
                            titleMap.put(newkey, 1);
                        }else{
                            titleMap.put(newkey, titleMap.get(newkey)+1);
                        }
                        
                    }
                    if(key.equals("Log7  Found book with author:")){
                        if(!authorMap.containsKey(newkey)){
                            authorMap.put(newkey, 1);
                        }else{
                            authorMap.put(newkey, authorMap.get(newkey)+1);
                        }
                    }
                    if(key.equals("Log2  device type:")){
                        if(!deviceMap.containsKey(newkey)){
                            deviceMap.put(newkey, 1);
                        }else{
                            deviceMap.put(newkey, deviceMap.get(newkey)+1);
                        }
                    }
                    j++;
                }
                Log l=new Log(record);
                logs.add(l);
            }
        }
        //add the full log information to jsp attribute
        request.setAttribute("logList", logs);
        //convet the four maps to List to sort in descending order
        List<Map.Entry<String, Integer>> searchList = new ArrayList<Map.Entry<String,Integer>>(searchMap.entrySet());
        List<Map.Entry<String, Integer>> titleList = new ArrayList<Map.Entry<String,Integer>>(titleMap.entrySet());
        List<Map.Entry<String, Integer>> authorList = new ArrayList<Map.Entry<String,Integer>>(authorMap.entrySet());
        List<Map.Entry<String, Integer>> deviceList = new ArrayList<Map.Entry<String,Integer>>(deviceMap.entrySet());
        //sort four lists
        Collections.sort(searchList,valComparator);
        Collections.sort(titleList,valComparator);
        Collections.sort(authorList,valComparator);
        Collections.sort(deviceList,valComparator);
        //cut the four list to top 3 results
        //top 3 search term:
        searchList=searchList.subList(0, Math.min(3, searchList.size()));
        titleList=titleList.subList(0, Math.min(3, titleList.size()));
        authorList=authorList.subList(0, Math.min(3, authorList.size()));
        deviceList=deviceList.subList(0, Math.min(3, deviceList.size()));
        //set attribute for the jsp to get 
        request.setAttribute("searchList", searchList);        
        request.setAttribute("titleList", titleList); 
        request.setAttribute("authorList", authorList); 
        request.setAttribute("deviceList", deviceList); 

        String nextView= "result.jsp";

        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
    Comparator<Map.Entry<String, Integer>> valComparator = new Comparator<Map.Entry<String,Integer>>() {
        @Override
        public int compare(Entry<String, Integer> e1,Entry<String, Integer> e2) {
            return e2.getValue()-e1.getValue();// descending order sort the map
        }
    };
}
