package org.gpfister.blogMigrator.wordpress;

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
