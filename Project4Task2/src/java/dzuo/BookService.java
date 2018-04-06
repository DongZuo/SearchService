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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

// This example demonstrates Java servlets and HTTP
@WebServlet(name = "BookSearch", urlPatterns = {"/BookSearch/*"})
public class BookService extends HttpServlet {
    // MongoDB  collection to use
    MongoCollection<Document> collection;
    @Override
    public void init(){
        //connect to mongoDB database
        MongoClientURI connectionString = new MongoClientURI("mongodb://mongouser:mongopass@ds123399.mlab.com:23399/project4mongo");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("project4mongo");
        collection = database.getCollection("books");
    }
    
    // GET returns a list of book information in JSON format  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //these strings are used for log:
        String visitTime="null";
        String mobileDevice="null";
        String searchTerm="null";
        String api_url="null";
        String reply="null";
        String bookTitle="null";
        String bookAuthor="null";
        String replyCode="401";
        //I learned how to get current time in date format from this reference:
        //https://stackoverflow.com/questions/833768/java-code-for-getting-current-time?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Log1  doGET visited at time :"+time.format(calendar.getTime()));
        visitTime=time.format(calendar.getTime());
        String result = "";
        String browserType = request.getHeader("User-Agent");
        String device=browserType.split(" ")[0];
        System.out.println("Log2  device type:"+device);
        mobileDevice=device;
        // The name is on the path /name so skip over the '/'
        String bookName = (request.getPathInfo()).substring(1);
        System.out.println("Log3  user entered search term:"+bookName);    
        searchTerm=bookName;
        // return 401 if bookname not provided
        if(bookName.equals("")) {
            response.setStatus(401);
            return;      
        }
               
        // Look up the name through the Google Book API
        //Here I use Jackson to parse JSON, I learn this method from Stackoverflow:
        //reference: https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        ObjectMapper mapper = new ObjectMapper();
        String url="https://www.googleapis.com/books/v1/volumes?q="+bookName+"+"+"intitle:"+bookName+"&key=AIzaSyAlV2Vph1TQ3rRJtveLh5RBqU8Q9WgygaA";
        url=url.replaceAll(" ", "%20");
        System.out.println("Log4  3-party API url:"+url);  
        api_url=url;
        URL u=new URL(url);
        Map<String,Object> map = mapper.readValue(u, Map.class);
        //this list is parsed from the JSON sent by Google Books API
        ArrayList items=(ArrayList)map.get("items");
        // return 401 if no records are found by Google API
        if(items == null || items.size()==0) {
            response.setStatus(401);
            reply="failure";
            System.out.println("Log5  No reply from 3-party API.");
            return;    
        }else{
            reply="success";
            System.out.println("Log5  Successfully get reply from 3-party API.");
        }
        //this json object is used to send our JSON message to the Android Application
        JSONObject js = new JSONObject();
        LinkedHashMap<String,Object> item=(LinkedHashMap<String,Object>) items.get(0);
        item=(LinkedHashMap<String, Object>) item.get("volumeInfo");
        String title=(String)item.get("title");
        System.out.println("Log6  Found book with title:"+title);
        bookTitle=title;
        js.put("title", title);
//            System.out.print("title:"+title+"  ,  ");
        if(((ArrayList)item.get("authors"))!=null&&((ArrayList)item.get("authors")).size()>0){
            String author=(String)((ArrayList)item.get("authors")).get(0);
            System.out.println("Log7  Found book with author:"+author);
            bookAuthor=author;
            js.put("author", author);
//                System.out.print("author:"+author+"  ,  ");
        }
        String rate=String.valueOf(item.get("averageRating"));
        js.put("rating", rate);
//            System.out.println("rating:"+rate+"\n");
        LinkedHashMap<String,Object>images=(LinkedHashMap<String, Object>)item.get("imageLinks");
        String imgUrl=(String)images.get("thumbnail");
        js.put("imgUrl",imgUrl);
        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");
        // return the JSON format data from a GET request
        PrintWriter out = response.getWriter();
        out.println(js.toString()); 
        System.out.println("Log8  Reply to the mobile user with status code :"+"200");
        replyCode="200";
        //update log to database:
        Document log=createDocument(visitTime,mobileDevice,searchTerm,api_url,reply,bookTitle,bookAuthor,replyCode);
        collection.insertOne(log);

    }


    public static Document createDocument(String time,String device,String search,String url,String reply,String title,String author,String code) throws MalformedURLException, IOException{
        Document document = new Document("Log1  doGET visited at time :", time);
        document.append("Log2  device type:",device);
        document.append("Log2  device type:",device);            
        document.append("Log3  user entered search term:",search);
        document.append("Log4  3-party API url:",url);
        document.append("Log5  reply from 3-party API:",reply);            
        document.append("Log6  Found book with title:",title);
        document.append("Log7  Found book with author:",author);            
        document.append("Log8  Reply to the mobile user with status code :",code);    
        return document;
    }
}
