title=New Solr-Jetty-Maven Release (0.1.0)
date=2015-06-16
type=post
tags=blog
status=published
~~~~~~

This release resolves some build issues that were a result of the [Solr-ISO639-Filter](https://github.com/ksclarke/solr-iso639-filter) dependency leaking some of its internal logging dependencies (which caused a conflict when trying to use Logback in Solr-Jetty-Maven).  It also adds support for all versions of Solr up to 4.10.4.  I expect this will be the last release of Solr-Jetty-Maven (in its current form at least).  The (somewhat) newly released Solr 5.x makes Solr much easier to use (which was something Solr-Jetty-Maven tried to address).

The next iteration of Solr-Jetty-Maven might be a pure Java form of the [Travis-Solr](https://github.com/ksclarke/travis-solr) Bash project (i.e., a Maven plugin that allows Solr to be spun up as a part of the testing phrase in a Maven build).  We'll see.
