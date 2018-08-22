# BlogMigrator

	© 2018, Greg PFISTER. MIT License.

Simple migration from Tumblr to Wordpress.

## Version 1.2 - August 2018

  - (Add) New feature to map tags
  
In order to allow for harmonisation of the tags, it is possible to convert tags to the expected list.

## Version 1.1 - July 2018

  - (Add) New feature to download media and map the URL for the upload.
  - (Add) Controls on configuration file required field and better handling of errors in the Tumblr JSON files.
  - (Fix) Mapping of the slug
  
Tumblr saves images with a generated ID as filename. With this function, images can be downloaded and renamed using the post slug. Then the file can be moved on a server (and the URL can be mapped accordingly).

There was not a log of control in case of there was missing required parameters in the config files. Also, there was not a lot of handling in case of the Tumblr JSON file had an unexpected structure.

There was an issue that slug won't be mapped even if it was forced.

## Version 1.0 - July 2018

Original version: only support single image photo posts and exclude post shared from Instagram.