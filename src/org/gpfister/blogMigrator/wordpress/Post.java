package org.gpfister.blogMigrator.wordpress;

import javax.xml.bind.annotation.XmlAttribute;  
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
public class Post {

	private long id = 0;
	private String title = null;
	private String author = null;
	private String timeStamp = null;
	private String content = null;
	private String slug = null;
	private String excerpt = null;
	private String categories = null;
	private String tags = null;
	
	public Post() {
		super();
	}
	
	/**
	 * @return the id
	 */ 
	@XmlAttribute 
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	@XmlElement
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the author
	 */
	@XmlElement
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * @return the timeStamp
	 */
	@XmlElement
	public String getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * @return the content
	 */
	@XmlElement
	public String getContent() {
		return content;
	}
	
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * @return the slug
	 */
	@XmlElement
	public String getSlug() {
		return slug;
	}
	
	/**
	 * @param slug the slug to set
	 */
	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	/**
	 * @return the excerpt
	 */
	@XmlElement
	public String getExcerpt() {
		return excerpt;
	}

	/**
	 * @param excerpt the excerpt to set
	 */
	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	/**
	 * @return the categories
	 */
	@XmlElement
	public String getCategories() {
		return categories;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(String categories) {
		this.categories = categories;
	}

	/**
	 * @return the tags
	 */
	@XmlElement
	public String getTags() {
		return tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}
	
}