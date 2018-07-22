package org.gpfister.blogMigrator.wordpress;

import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement; 

/**
 * BlogMigrator - https://github.com/gpfister/blogmigrator
 * 
 * @author Greg PFISTER
 * @license MIT License v2
 *
 */
@XmlRootElement
public class PhotoPost extends Post {

	private String images = null;
	
	public PhotoPost() {
		super();
	}

	/**
	 * @return the images
	 */
	@XmlElement
	public String getImages() {
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(String images) {
		this.images = images;
	}
}
