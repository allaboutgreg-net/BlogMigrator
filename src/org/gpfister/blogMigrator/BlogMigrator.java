package org.gpfister.blogMigrator;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.gpfister.blogMigrator.wordpress.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * BlogMigrator - https://github.com/gpfister/BlogMigrator
 * 
 * @author Greg PFISTER
 * @license MIT License
 *
 */
public class BlogMigrator {

	public static void main(String[] args) {
		
		if (args.length != 1)	{
			System.err.println("Usage: org.gpfister.blogMigrator.BlogMigrator <config_filename.json>");
			return;
		}
		
		Blog blog = new Blog();
		
        JSONParser parser = new JSONParser();
 
        try {
 
        	// Parse the tumblr.json
        	JSONObject jsonConfig = (JSONObject) parser.parse(new FileReader(args[0]));
        	
        	// Get the filenames
        	JSONArray jsonInputFiles = (JSONArray) jsonConfig.get("input_files");
        	Iterator<String> inputFilesIterator = jsonInputFiles.iterator();
        	
        	// Get the post mapping
        	JSONObject jsonPostMappingRules = (JSONObject) jsonConfig.get("post_mapping_rules");
        	boolean mustImportUnmapped = (boolean) jsonPostMappingRules.get("import_unmapped");
        	boolean mustMapIfEmptyOnly = (boolean) jsonPostMappingRules.get("map_if_empty_only");
        	
        	// Process each file
        	int postCount = 0;
        	int ignoredPostCount = 0;
        	while (inputFilesIterator.hasNext())	{
        		String input_file = (String) inputFilesIterator.next();
        		
        		System.out.println("Start processing " + input_file);
        		
        		// Parse the file
        		JSONObject jsonTumblrData = (JSONObject) parser.parse(new FileReader(input_file));
            	
            	// Get the posts
            	JSONObject response = (JSONObject) jsonTumblrData.get("response");
            	JSONArray jsonPosts = (JSONArray) response.get("posts");
            	Iterator<JSONObject> postIterator = jsonPosts.iterator();
            	
            	// Start analysing posts
            	while (postIterator.hasNext()) {
            		postCount++;
                    JSONObject jsonPost = (JSONObject) postIterator.next();
                    Long id = (Long) jsonPost.get("id");
                    String type = (String) jsonPost.get("type");
                    String linkURL = (String) jsonPost.get("link_url");
                    String slug = (String) jsonPost.get("slug");
                    String summary = (String) jsonPost.get("summary");
                    String caption = (String) jsonPost.get("caption");
                    String date = (String) jsonPost.get("date");
                    JSONArray jsonTags = (JSONArray) jsonPost.get("tags");
                    Iterator<String> tagsIterator = jsonTags.iterator();
                    
                    // Get post mapping data
                    String strId = id.toString();
                    
                    
                    // Based on post type
                    switch (type)	{
	                    case "photo":
	                    	
	                    	// Get photos
	                    	JSONArray jsonPhotos = (JSONArray) jsonPost.get("photos");
                        	Iterator<JSONObject> photosIterator = jsonPhotos.iterator();
	                    	
	                    	// Check if post meet all criteria
	                    	if (linkURL != null && ( linkURL.contains("instagr.am") || linkURL.contains("instagram")))	{
	                        	ignoredPostCount++;             
	                        	//System.out.println("Ignoring post " + strId + " (Instagram).");
	                    	} else if (jsonPhotos != null && jsonPhotos.size() > 1)	{
	                        	ignoredPostCount++;
	                        	//System.out.println("Ignoring post " + strId + " (Photoset).");
	                        } else if (jsonPhotos != null && jsonPhotos.size() == 1) {
	                        	// Get post mapping
	                        	JSONObject postMapping = (JSONObject) jsonPostMappingRules.get(strId);
		                    	
		                    	// Initialise photo post
		                    	PhotoPost photoPost = new PhotoPost();
		                    	photoPost.setId(id.longValue());
		                    	photoPost.setTitle((postMapping != null ? (String) postMapping.get("title") : null));
		                    	photoPost.setSlug((! mustMapIfEmptyOnly && postMapping != null ? (String) postMapping.get("slug") : slug));
		                    	photoPost.setExcerpt(summary);
		                    	photoPost.setContent(caption);
		                    	photoPost.setTimeStamp(date);
		                    	photoPost.setCategories((postMapping != null ? (String) postMapping.get("categories") : null));
		                    	
		                    	// Set tags
		                    	while (tagsIterator.hasNext())	{
		                    		if (photoPost.getTags() != null)	photoPost.setTags(photoPost.getTags() + ", " + tagsIterator.next());
		                    		else								photoPost.setTags(tagsIterator.next());
		                    	}
		                    	
	                            if (postMapping == null && ! mustImportUnmapped)	{
	                            	ignoredPostCount++;
	                            	System.err.println("Ignoring post " + strId + " (unmapped).");
	                            } else {
		                        	JSONObject jsonPhoto = photosIterator.next();
		                        	JSONObject jsonOriginalSizePhoto = (JSONObject) jsonPhoto.get("original_size");
		                        	String url = (String) jsonOriginalSizePhoto.get("url");
	
	                        		// Add image to photo post
		                        	photoPost.setImages(url);
		                        	
		                        	// Add photo post to blog	                        	
		                        	blog.addPhotoPost(photoPost);
	                            }
	                        }
	                    	break;
	                    case "video":
	                    	if (linkURL != null && ( linkURL.contains("instagr.am") || linkURL.contains("instagram")))	{
	                        	ignoredPostCount++;             
	                        	//System.out.println("Ignoring post " + strId + " (Instagram).");
	                    	}
	                    	break;
	                    default:
	                    	ignoredPostCount++;             
	                    	//System.out.println("Ignoring post " + strId + " (wrong type " + type + ").");
                    }
                }
            }
        	
        	// Create Java XML binding contextual object
        	JAXBContext jaxbContext = JAXBContext.newInstance("org.gpfister.blogMigrator.wordpress");
        	
        	// Create marshaller object
            Marshaller marshaller = jaxbContext.createMarshaller();  
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Write file
            String output_file = (String) jsonConfig.get("output_file");
            System.out.println("Generate output file " + output_file);
            marshaller.marshal(blog, new FileOutputStream(output_file)); 
        	
        	// Summary
        	System.out.println("Counted " + postCount + " post(s). Ignored " + ignoredPostCount + " post(s).");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
