# solr-jetty-maven <a href="https://travis-ci.org/ksclarke/solr-jetty-maven"><img src="https://travis-ci.org/ksclarke/solr-jetty-maven.png?branch=master"/></a>

A Solr project that runs in a Jetty container in a Maven 3 environment. It also contains a Solr Maven plugin that allows you to create and delete cores.  This project currently supports Solr version 4.8.

### Introduction

To install, you'll need Git, Java (>= 7), and Maven. Once those are installed and setup, you can download the solr-jetty-maven code using Git:

    git clone git://github.com/ksclarke/solr-jetty-maven.git

To start a standalone/master Solr instance, change into the project directory and type:

    mvn install jetty:run

To start a slave Solr instance, edit the <master.solr.url> element in the <a href="https://github.com/ksclarke/solr-jetty-maven/blob/master/pom.xml">pom.xml</a> to point to the master and then type:

    mvn -Denv.type=slave process-resources jetty:run

To use Solr's admin/dashboard UI, go to: <a href="http://localhost:8983/solr/#/">http://localhost:8983/solr/#/</a>

In order to run a master and slave on the same machine, you'll need to tweak Jetty's settings in the pom.xml file. The default setup is intended for the most common case where the master and slave will be on different machines.

### Installation and Configuration
  
For installing on a server, you might want to follow these steps (feel free to tweak them to fit your specific environment or to ignore them altogether):

    git clone git://github.com/ksclarke/solr-jetty-maven.git
    mv solr-jetty-maven /opt
    sudo chown -R apache:apache /opt/solr-jetty-maven

You can then run the server using the Web user on your machine:

    sudo -u apache mvn install jetty:run

Or, you can run it as a system service using the steps in the "Running as a System Service" section below.

To customize the configuration of a Solr master or slave, look at the config properties in the project's pom.xml profiles section. These values can be overridden (without touching the pom.xml file) in a Maven settings.xml file found at either of the following locations:

    $M2_HOME/conf/settings.xml
    ${user.home}/.m2/settings.xml

If neither of these settings files exist, you can create one. See the <a href="http://maven.apache.org/settings.html">Maven documentation</a> for what a settings.xml file should contain.

### Creating and Deleting Solr Cores

There is a default core that comes configured called 'collection1' but if you want to create other cores there are two Maven goals that can be used.

To create a new core (before you start the Jetty server), type:

    mvn info.freelibrary:solr-jetty-maven:create-core -Dsolr.core.name=corename

To delete an old core (after the Jetty server has been stopped), type:

    mvn info.freelibrary:solr-jetty-maven:delete-core -Dsolr.core.name=corename

If you want to save yourself some typing, you can add the following markup to your settings.xml file:

    <pluginGroups>
      <pluginGroup>info.freelibrary</pluginGroup>
    </pluginGroups>

Then you will be able to run the Solr goals by just typing:

    mvn solr:create-core -Dsolr.core.name=corename

and

    mvn solr:delete-core -Dsolr.core.name=corename

To see cores you've created (once you've started Jetty), you can go to the administrative Web interface: <a href="http://localhost:8983/solr/#/~cores/">http://localhost:8983/solr/#/~cores/</a>

### Running as a System Service

There is a beta init.d script that will allow the Solr service to be started in a standard Linux way. First, check to ensure that the user who will be running the service, the location of the service (i.e., where you've checked out the project's code), and all the other init.d script's variables reflect the values of your local system.

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

### Replication Options

With Solr 4.x, there is a new SolrCloud option that allows Solr to use an automated approach to sharding. After reviewing it, I've concluded the use case it supports is beyond the scope of what I want to accomplish with this project's Solr-in-Maven approach. This project supports the use of slaves to scale out a search service, but it will not support the new SolrCloud / Zookeeper setup.

### Warning for those familiar with Maven

Note that, currently, running:

    mvn clean

will "clean" (i.e., delete) the Solr workspace (index, snapshots, etc.)

### License

Apache Software License, version 2.0 (just like the projects it's based on)

### Contact

This is a pretty straightforward use of Solr, but if you have questions about it feel free to contact me, Kevin S. Clarke, at ksclarke@gmail.com
