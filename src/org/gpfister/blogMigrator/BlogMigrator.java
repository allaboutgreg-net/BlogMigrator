package org.gpfister.blogMigrator;

import org.gpfister.blogMigrator.wordpress.*;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FilenameUtils;
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
        	if (jsonConfig == null)	{
        		System.err.println("Unable to process file " + args[0]);
        		return;
        	}
        	
        	// Get the filenames
        	JSONArray jsonInputFiles = (JSONArray) jsonConfig.get("input_files");
            String output_file = (String) jsonConfig.get("output_file");
        	Iterator<String> inputFilesIterator = null;
        	if (jsonInputFiles != null)	{
        		inputFilesIterator = jsonInputFiles.iterator();
        	}
        	if (jsonInputFiles == null || ! inputFilesIterator.hasNext() || output_file == null) {
        		System.err.println("Input file(s) or output file not defined");
        		return;
        	}
        	
        	// Get the post mapping rules
        	JSONObject jsonPostMappingRules = (JSONObject) jsonConfig.get("post_mapping_rules");
        	JSONObject jsonTagMapping= null;
    		boolean mustImportUnmapped = false;
    		boolean mustMapIfEmptyOnly = false;
    		String mappingMediaDownloadURL = null;
        	if (jsonPostMappingRules != null)	{
        		jsonTagMapping = (JSONObject) jsonPostMappingRules.get("tag_mapping");
        		mustImportUnmapped = (boolean) jsonPostMappingRules.get("import_unmapped");
        		mustMapIfEmptyOnly = (boolean) jsonPostMappingRules.get("map_if_empty_only");
        		mappingMediaDownloadURL = (String) jsonPostMappingRules.get("map_media_download_url");
        	}
        	
        	// Get the instruction for media download
        	JSONObject jsonMediaDownloadRules = (JSONObject) jsonConfig.get("media_download_rules");
        	boolean mustUseSlugAsMediaFilename = false;
        	String mediaDownloadFolder = null;
        	if (jsonMediaDownloadRules != null)	{
        		mustUseSlugAsMediaFilename = (boolean) jsonMediaDownloadRules.get("use_slug_as_media_filename");
        		mediaDownloadFolder = (String) jsonMediaDownloadRules.get("media_download_folder");
        		if (mediaDownloadFolder == null) {
            		System.err.println("Media download folder required");
            		return;
            	}
        	}
        	
        	// Process each file
        	int postCount = 0;
        	int ignoredPostCount = 0;
        	while (inputFilesIterator.hasNext())	{
        		String input_file = (String) inputFilesIterator.next();
        		
        		System.out.println("Start processing " + input_file);
        		
        		// Parse the file
        		JSONObject jsonTumblrData = (JSONObject) parser.parse(new FileReader(input_file));
            	
            	// Get the response part
            	JSONObject jsonResponse = (JSONObject) jsonTumblrData.get("response");
            	if (jsonResponse != null)	{
            		
            		// Get the posts
	            	JSONArray jsonPosts = (JSONArray) jsonResponse.get("posts");
	            	if (jsonPosts != null)	{
		            	
		            	// Start analysing posts
		            	Iterator<JSONObject> postIterator = jsonPosts.iterator();
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
			                        	
			                        	// Get post mapping
			                        	String newCategories = null;
			                        	String newTitle = null;
			                        	String newSlug = null;
				                    	if (postMapping != null)	{
				                    		newCategories = (String) postMapping.get("categories");
				                    		newTitle = (String) postMapping.get("title");
				                    		newSlug = (String) postMapping.get("slug");
				                    	}
				                    	
				                    	// Initialise photo post
				                    	PhotoPost photoPost = new PhotoPost();
				                    	photoPost.setId(id.longValue());
				                    	photoPost.setTitle(newTitle);
				                    	photoPost.setSlug((newSlug != null ? (slug == null || ! mustMapIfEmptyOnly ? newSlug : slug) : slug));
				                    	photoPost.setExcerpt(summary);
				                    	photoPost.setContent(caption);
				                    	photoPost.setTimeStamp(date);
				                    	photoPost.setCategories(newCategories);
				                    	
				                    	// Set tags
				                    	while (tagsIterator.hasNext())	{
				                    		String tag = tagsIterator.next();
				                    		if (jsonTagMapping != null)	{
				                    			String newTag = (String) jsonTagMapping.get(tag);
				                    			if (newTag != null)	tag = newTag;
				                    		}
				                    		if (photoPost.getTags() != null)	photoPost.setTags(photoPost.getTags() + ", " + tag);
				                    		else								photoPost.setTags(tag);
				                    	}
				                    	
			                            if (postMapping == null && ! mustImportUnmapped)	{
			                            	ignoredPostCount++;
			                            	System.err.println("Ignoring post " + strId + " (unmapped).");
			                            } else {
				                        	JSONObject jsonPhoto = photosIterator.next();
				                        	JSONObject jsonOriginalSizePhoto = (JSONObject) jsonPhoto.get("original_size");
				                        	String url = (String) jsonOriginalSizePhoto.get("url");
				                        	
				                        	if (jsonMediaDownloadRules != null)	{
				                        		String filename = BlogMigrator.downloadMedia(url, mediaDownloadFolder, (mustUseSlugAsMediaFilename ? photoPost.getSlug() : null));
				                        		if (mappingMediaDownloadURL != null)	{
				                        			url = mappingMediaDownloadURL + filename;
				                        		}
				                        	}
			
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
            	}
            }
        	
        	// Create Java XML binding contextual object
        	JAXBContext jaxbContext = JAXBContext.newInstance("org.gpfister.blogMigrator.wordpress");
        	
        	// Create marshaller object
            Marshaller marshaller = jaxbContext.createMarshaller();  
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Write file
            System.out.println("Generate output file " + output_file);
            marshaller.marshal(blog, new FileOutputStream(output_file)); 
        	
        	// Summary
        	System.out.println("Counted " + postCount + " post(s). Ignored " + ignoredPostCount + " post(s).");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static String downloadMedia(String strURL, String downloadFolderPath, String newFilename)	{	
		String filename = null;
		
		try	{
			URL url = new URL(strURL);
			
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			
			filename = FilenameUtils.getName(url.getPath());
			if (newFilename != null)	{
				filename = newFilename + "." + FilenameUtils.getExtension(url.getPath());
			}
			
			FileOutputStream fos = new FileOutputStream(downloadFolderPath + filename);
			
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (Exception e) {
            e.printStackTrace();
        }
		
		return filename;
	}
}
