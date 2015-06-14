title=Solr-Jetty-Maven Build Refactored
date=2015-06-14
type=post
tags=blog
status=published
~~~~~~

The project's build has been refactored to move much of the pom.xml file's configuration up into a FreeLibrary parent project (that this project now extends).  In addition, some of the site reports have been moved out of the Maven build and into an external SonarQube site. The site generation tool has also been changed to JBake.
