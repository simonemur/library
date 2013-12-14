/**
 *
 * FENIX (Food security and Early warning Network and Information Exchange)
 *
 * Copyright (c) 2011, by FAO of UN under the EC-FAO Food Security
Information for Action Programme
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.fao.fenix.metadata.manager.rest;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.fao.fenix.metadata.manager.jdbc.MongoDBConnectionManager;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("/library")
public class SQLRESTService {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/post/books")
	public Response postView(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("schema") String schema,
            @FormParam("collection") String collection,
            @FormParam("json") String json) {

        System.out.println("STORE BOOKS");
        System.out.println(schema);
        System.out.println(collection);
        System.out.println(json);
        try {
            System.out.println(json);
            Gson g = new Gson();
            saveJson(schema, collection, json);
			String out = g.toJson("JSON successfully stored.");
			ResponseBuilder builder = Response.ok(out);
			builder.header("Access-Control-Allow-Origin", "*");
			builder.header("Access-Control-Max-Age", "3600");
			builder.header("Access-Control-Allow-Methods", "POST");
			builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");
			return builder.build();
		} catch (Exception e) {
			return Response.status(500).entity("Error in 'getView' service: " + e.getMessage()).build();
		}
	}

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/books")
    public Response getBooksTitle(
            @FormParam("title") String title,
            @FormParam("author") String author,
            @FormParam("isbn") String isbn,
            @FormParam("schema") String schema,
            @FormParam("collection") String collection) {

        System.out.println("library");
        System.out.println(schema);
        System.out.println(collection);
        System.out.println(title);
        System.out.println(author);
        try {
            Gson g = new Gson();
            String view = selectBooks(schema, collection, title, author, isbn);
            ResponseBuilder builder = Response.ok(view);
            builder.header("Access-Control-Allow-Origin", "*");
            builder.header("Access-Control-Max-Age", "3600");
            builder.header("Access-Control-Allow-Methods", "POST");
            builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");
            return builder.build();
        } catch (Exception e) {
            return Response.status(500).entity("Error in 'getView' service: " + e.getMessage()).build();
        }
    }
	
	private void saveJson(String schema, String collection, String json) throws UnknownHostException {
        MongoDBConnectionManager mgr = MongoDBConnectionManager.getInstance();
        Mongo mongo = mgr.getMongo();
		DB db = mongo.getDB(schema);
		DBCollection c = db.getCollection(collection);
		DBObject dbObject = (DBObject)JSON.parse(json);
		c.insert(dbObject);
	}

    private String selectBooks(String collectionType, String type, String text, String schema) throws UnknownHostException {
        System.out.println(type);
        System.out.println(schema);
        List<String> l = new ArrayList<String>();
        MongoDBConnectionManager mgr = MongoDBConnectionManager.getInstance();
        Mongo mongo = mgr.getMongo();
        DB db = mongo.getDB(schema);
        // collectionType = "view"
        try {
            DBCollection collection = db.getCollection(collectionType);
            BasicDBObject searchQuery = new BasicDBObject();
            BasicDBObject regexQuery = new BasicDBObject();
            //regexQuery.put("title", new BasicDBObject("$regex", "^.*mi.*$").append("$options", "i"));

            regexQuery.put(type, new BasicDBObject("$regex", text).append("$options", "i"));

            DBCursor cursor = collection.find(regexQuery);
            while (cursor.hasNext())
                l.add(cursor.next().toString());
        }catch (Exception e ) {
            System.out.println(e.getMessage());
        }
        System.out.println(l);
        if ( l.size() > 0 )
            return l.toString();
        return "error";
    }

    private String selectBooks(String schema, String collection, String title, String author, String isbn) throws UnknownHostException {
        System.out.println(schema);
        System.out.println(collection);
        System.out.println(title);
        System.out.println(author);
        List<String> l = new ArrayList<String>();
        MongoDBConnectionManager mgr = MongoDBConnectionManager.getInstance();
        Mongo mongo = mgr.getMongo();
        DB db = mongo.getDB(schema);

        try {
            DBCollection c = db.getCollection(collection);
            BasicDBObject titleQuery = new BasicDBObject();
            titleQuery.put("title", new BasicDBObject("$regex", title));
            BasicDBObject authorQuery = new BasicDBObject();
            authorQuery.put("author", new BasicDBObject("$regex", author));

            BasicDBList or = new BasicDBList();
            or.add(titleQuery);
            or.add(titleQuery);

            DBObject query = new BasicDBObject("$and", or);

            DBCursor cursor = c.find(titleQuery);
            while (cursor.hasNext())
                l.add(cursor.next().toString());
        }catch (Exception e ) {
            System.out.println(e.getMessage());
        }
        System.out.println(l);
        if ( l.size() > 0 )
            return l.toString();
        else return null;
    }


    private List<String> selectViewCodes(String schema) throws UnknownHostException {
        // Initiate variables
        List<String> l = new ArrayList<String>();
        MongoDBConnectionManager mgr = MongoDBConnectionManager.getInstance();
        Mongo mongo = mgr.getMongo();
        DB db = mongo.getDB(schema);
        DBCollection collection = db.getCollection("views");

        // Compose NoSQL query
        BasicDBObject groupFiels  = new BasicDBObject("_id", "$view_id");
        BasicDBObject group  = new BasicDBObject("$group", groupFiels);
        BasicDBObject sortFiels  = new BasicDBObject("_id", 1);
        BasicDBObject sort  = new BasicDBObject("$sort", sortFiels);

        // Create the output
        AggregationOutput output = collection.aggregate(group, sort);
        Iterable<DBObject> results = output.results();
        for (DBObject result : results) {
            try {
                l.add(result.get("_id").toString());
            } catch (NullPointerException e) {
                System.out.println("Skipping NULL");
            }
        }

        // Return the result
        return l;
    }

}