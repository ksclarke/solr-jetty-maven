### Introduction

Solr-Jetty-Maven is a simple Maven wrapper around the Solr search service.  It allows Solr to be run from within a Maven project, using Jetty as the server.  It's designed to provide a drop dead simple way to run Solr, but it also includes init.d scripts so that it can be run in a more traditional way (i.e., as a system service on a Linux machine).

Just as a note, there isn't much original code in this project.  Most of its value is in the Maven configuration.  There are a couple of Maven mojos, to make things like creating and deleting new Solr cores easier (see below for information on using them), but that's about it.

**[ Update:** With Solr 4.x, there is a new SolrCloud option that allows Solr to use an automated approach to sharding. After reviewing it, I've concluded the use case for it is beyond the scope of what I want to accomplish with this project. This project supports the use of slaves to scale out a search service, but it will not attempt to support the SolrCloud / Zookeeper stack. **]**

_This project is not connected with the Solr project in any way.  It's just an attempt to repackage it in a clean, simple, native-Maven way._

_If you're looking for information about Solr, you'll want to <a href="http://lucene.apache.org/solr/">visit their website</a>._

### Getting Started

To install, you'll need Git, Java (>= 7), and Maven (>= 3). Once those are installed and setup, you can <a href="https://github.com/ksclarke/solr-jetty-maven/archive/master.zip">download</a> the solr-jetty-maven code directly or clone it using Git:

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
