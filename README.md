# BlogMigrator

	© 2018, Greg PFISTER. MIT License.

Simple migration from Tumblr to Wordpress.

## Description.

BlogMigrator is a simple Java program to convert Tumblr JSON files to an XML to import in Wordpress. The JSON files are downloaded calling the [Tumblr API](https://www.tumblr.com/docs/en/api/v2) (query Posts). It then generates a custom XML file which can then be loaded using the plugin [WP All Import](http://www.wpallimport.com).

It may not be usable as is for your blog migration, but you can fork to make your own version.

### Tumblr API

In order to get the JSON file, the query https://api.tumblr.com/v2/blog/<blog URI>/posts must be run (using a program like the free version of [Postman](https://www.getpostman.com)).

This query will require to obtain an authentication token.

This query will return 20 posts at the time. In order to get the next 20 posts, use the `offset` parameter:

For example, if there are 75 posts on your Tumblr blog:

  - The first 20: https://api.tumblr.com/v2/blog/<blog URI>/posts
  - The next 20: https://api.tumblr.com/v2/blog/<blog URI>/posts?offset=20
  - The next 20: https://api.tumblr.com/v2/blog/<blog URI>/posts?offset=40
  - The last 15: https://api.tumblr.com/v2/blog/<blog URI>/posts?offset=60
  
You can of course explore the Tumblr API to make the appropriate queries.

### Build and run

Eclipse Photon was used to build and run. There are Maven dependencies.

The program requires only one parameter: the path to the JSON configuration file. For details about this file, please refer to the next section.

### JSON Configuration

This file will have all the parameter to convert the JSON files from Tumblr to an XML file.

	{
		"input_files": [
			"/path/to/tumblr/input/file1.json",
			"/path/to/tumblr/input/file2.json",
			"/path/to/tumblr/input/file3.json",
			"/path/to/tumblr/input/file4.json"
		],
		"output_file": "/path/to/output/file.xml",
		"media_download_rules": {
			"use_slug_as_media_filename": false,
			"media_download_folder": "/path/to/media/download/folder/"
		},
		"post_mapping_rules": {
			"import_unmapped": false,
			"map_if_empty_only": false,
			"map_media_download_url": "http://new.url.for/media/files/",
			"id-1": {
				"categories": "new, categories",
				"title": "New title 1",
				"slug": "new-title-1"
			},
			"id-2": {
				"categories": "new, categories",
				"title": "New title 2",
				"slug": "new-title-2"
			}
		}
	}

Here are the details about the file.

`input_files` (mandatory): an array of file path to the Tumblr JSON files. They will be processed in this sequence.

`output_file` (mandatory): the path to the output file.

'media_download_rules' (optional): a set of rules for downloading medias. If missing, media won't be downloaded.

`media_download_rules.use_slug_as_media_filename` (optional): if set to `true`, downloaded media file will be renamed using the post `slug`. Else filename is not changed.

`media_download_rules.media_download_folder` (mandatory): the path to the destination download folder.

`post_mapping_rules` (optional): a set of rules to apply during the conversion:


`post_mapping_rules.import_unmapped` (optional): if set to `true`, only import a post if there's a specific mapping rule for the post `id`. Else, don't filter the post.


`post_mapping_rules.map_if_empty_only` (optional): if set to `true`, only replace a value in a field if this field is originaly empty. Else, the value will be overwritten.

`post_mapping_rules.id.map_media_download_url` (optional): the mapping of the URL (without the file name) of the media. It can be used if file must be put on a different server to easier access, or if renaming using slug (set the `media_download_rules` part). If not set, the original source URL will be used.

`post_mapping_rules.id` (optional): allow to set specific rules for a given post `id`

`post_mapping_rules.id.categories` (optional): the categories to assign to the post (Tumblr doesn't use categories).

`post_mapping_rules.id.title` (optional): the title to assign to the post (Tumblr doesn't use title).

`post_mapping_rules.id.slug` (optional): the slug for the URL (Tumblr uses slug as well).
     
### XML Output and import process

There is no schema (XSD or DTD) for the output XML. It is generated using JAXB. The plugin `WP All Import` doesn't require a fix format, it will allow you to map a custom XML to each object you are trying to load.

## Dependencies

This program requires `JSON-Simple` and `JAXB`.

To build the code, only the following Maven dependencies must be resolved:

	<dependencies>
		<dependency>
			<groupId>com.googlecode.json-simple</groupId>
			<artifactId>json-simple</artifactId>
			<version>1.1.1</version>
		</dependency>
		<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-core</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-impl</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>javax.activation</groupId>
			<artifactId>activation</artifactId>
			<version>1.1.1</version>
		</dependency>
	</dependencies>
	
## License

This program is provided under the MIT License. You are free to fork it to make it your own, but you need to respect the MIT License.