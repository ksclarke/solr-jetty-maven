### Introduction

Solr-Jetty-Maven is a simple Maven wrapper around the Solr search service.  It allows Solr to be run from within a Maven project, using Jetty as the server.  It's designed to provide a drop dead simple way to run Solr, but it also includes init.d scripts so that it can be run as a system service on a Linux machine.

Just as a note, there isn't much original code in this project.  Most of its value is in the Maven configuration.  There are a couple of Maven Mojos, to enable the creating and deleting of new Solr cores (see below for information on using them), but that's about it.

<br/>
_This project is not connected with the Solr project in any way.  It's just an attempt to package Solr in a clean, simple, native-Maven way._

_If you're looking for information about Solr, you'll want to <a href="http://lucene.apache.org/solr/">visit their website</a>._

### Getting Started

To install, you'll need Java (>= 7), and Maven (>= 3). Once those are installed and setup, you can <a href="https://github.com/ksclarke/solr-jetty-maven/archive/master.zip">download a zip file</a> of the solr-jetty-maven code or clone it to your machine using Git:

    git clone git://github.com/ksclarke/solr-jetty-maven.git

To start a standalone/master Solr instance, change into the project directory and type:

    mvn install jetty:run

To start a slave Solr instance, edit the <master.solr.url> element in the <a href="https://github.com/ksclarke/solr-jetty-maven/blob/master/pom.xml">pom.xml</a> file to point to the master and then type:

    mvn -Denv.type=slave process-resources jetty:run

To use Solr's admin/dashboard UI, go to: http://localhost:8983/solr/#/

In order to run a master and slave on the same machine, you'll need to tweak Jetty's settings in the <a href="https://github.com/ksclarke/solr-jetty-maven/blob/master/pom.xml">pom.xml</a> file. The default setup is intended for the most common case where the master and slaves will be run on different (virtual) machines.

### Creating and Deleting Solr Cores

There is a default Solr core that comes configured called 'collection1', but if you want to create other cores there are two Maven goals that can be used.

To create a new core (before you start the Jetty server), type:

    mvn info.freelibrary:solr-jetty-maven:create-core -Dsolr.core.name=corename

To delete an old core (after the Jetty server has been stopped), type:

    mvn info.freelibrary:solr-jetty-maven:delete-core -Dsolr.core.name=corename

If you want to save yourself some typing, you can add the following markup to your <a href="http://maven.apache.org/settings.html" target="_new">settings.xml</a> file:

    <pluginGroups>
      <pluginGroup>info.freelibrary</pluginGroup>
    </pluginGroups>

Then you will be able to run the Solr goals by just typing:

    mvn solr:create-core -Dsolr.core.name=corename

and

    mvn solr:delete-core -Dsolr.core.name=corename

To see cores you've created (once you've started Jetty), you can go to the administrative Web interface: http://localhost:8983/solr/#/~cores/

### Running as a System Service on Linux

Included in this project is a beta init.d script that will allow Solr to be started in a standard Linux way. First, check to ensure that the user who will be running the service, the location of the service (i.e., where you've checked out the project's code), and all the other init.d script's variables reflect the values of your local system.

After that, you can install it by copying it to the /etc/init.d directory:

    sudo cp etc/init.d/solr /etc/init.d/solr

Then run `update-rc.d` to add the service to the desired system runlevels:

    sudo update-rc.d -f solr start 80 2 3 4 5 . stop 30 0 1 6 .

Once this is done, you should be able to start and stop the service:

    sudo service solr start
    sudo service solr status
    sudo service solr stop

You can remove the service from the system's runlevels by typing:

    sudo update-rc.d -f solr remove

### Adding Custom Filters and Analyzers

To add custom filters and analyzers distributed through the Maven repository, you just need to add them as dependencies in your pom.xml file.  For instance, the following would add the [solr-iso639-filter](/solr-iso639-filter "Solr ISO-639 Filter") to your project:
 
    <dependency>
      <groupId>info.freelibrary</groupId>
      <artifactId>solr-iso639-filter</artifactId>
      <version>4.4.0-r20130829</version>
    </dependency> 

You'll still need to configure its use in the project's <a href="https://github.com/ksclarke/solr-jetty-maven/blob/master/src/main/resources/solr/collection1/conf/schema.xml">schema.xml</a> file, of course.

### Caveats

Solr now offers a new replication option, <a href="http://wiki.apache.org/solr/SolrCloud">SolrCloud</a>.  This allows <a href="https://cwiki.apache.org/confluence/display/solr/Shards+and+Indexing+Data+in+SolrCloud">shards</a> to be split across multiple machines, using <a href="http://zookeeper.apache.org/">Zookeeper</a> to keep things in sync.

In keeping with its "easy to use" goal, this project doesn't support SolrCloud.  Instead, it supports the older master / slave Solr configuration, which allows Solr to scale by the addition of read-only slaves.

So, if your index won't fit on a single machine, this project won't work for you.

The other caveat is that this project stores its Solr data directory in the _target_ directory.  What this means is that running \`mvn clean\` will delete your Solr index, snapshots, logs, etc.

This is intentional.  It encourages the best practice of being able to rebuild your index from scratch.

You probably _don't_ want to do this on your production instance without some consideration to the result.