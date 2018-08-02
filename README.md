# PhotoManager

PhotoManager is a Java program that allows the user to manage, view, tag, and organize their photo repository on their local drive. The user can conveniently navigate through their selected photo repository, tag photos with tags that will appear in the file name, search through their repository for tagged images, and view the tag history at any time. It is an easy but powerful tool for photographers and artists alike!

# What It Does
PhotoManager is a Java program with a JavaFX interface with which the user interacts. On startup, the user chooses a directory in which their photo repository is located. They can then navigate through sub-directories of the root, and view the photos in the repository. The user can enter tags for the photos, and each photo can have multiple tags. The user can also manage tags separately, as well as searching by a specific tag in the whole repository. As a bonus feature, the program generates a suggested tag for each image based on the content of the image, by utilizing the Inception library and Tensorflow from Google.

# How It Works
The data store for the PhotoManager is generated on startup when the user specifies the root of the repository. Each image file and directory is represented by an object, and the directory tree is represented by a recursive definition, with each directory storing its children directories. The tags added to the photos are stored in the file name of the photos themselves along with the original file name, and on startup the program goes through every photo in the repository by recursion to update the database with the stored tags. The graphical user interface is implemented in JavaFX, using the Model-View-Controller design pattern.

# How To Run From The Command Line
Clone the repo then enter the target/classes folder. Run the following: ```java com.PhotoManager.GUInterface```

# Acknowledgements
This project was developed as the final project for CSC207: Software Design at the University of Toronto, St. George campus. Many thanks to our professor Paul Gries for his direction during the course, and the TAs who helped us out. 
