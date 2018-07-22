package org.gpfister.blogMigrator.wordpress;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement; 

/**
 * BlogMigrator - https://github.com/gpfister/BlogMigrator
 * 
 * @author Greg PFISTER
 * @license MIT License
 *
 */
@XmlRootElement
public class Blog {

	private List<PhotoPost> photoPosts = null;
	
	public Blog() {
		super();
		photoPosts = new ArrayList<PhotoPost>();
	}

	/**
	 * @return the posts
	 */
	@XmlElement
	public List<PhotoPost> getPhotoPosts() {
		return photoPosts;
	}

	/**
	 * @param posts the posts to set
	 */
	public void addPhotoPost(PhotoPost photoPost) {
		this.photoPosts.add(photoPost);
	}

	/**
	 * @param posts the posts to set
	 */
	public void removePhotoPost(PhotoPost photoPost) {		
		this.photoPosts.remove(photoPost);
	}
	
}
