# Bridge Logistics JMS module

You can find here installation information about how to get the services running
## ActiveMQ Installation

$ brew install activemq

## Running integration tests in IntelliJ

Make sure you have this running test environment for [Arquillian](http://arquillian.org/)

|Arquillian                                |
|------------------------------------------|
|Weld EE Embedded 1.1.x: DomainConsumerTest|

## Running with JBoss

This application is being tested with JBoss 8.0.0.Final
If you run accross this error:

```text
Caused by: org.jboss.msc.service.ServiceNotFoundException: Service service jboss.ejb.default-resource-adapter-name-service not found
```

Please make sure that your standalone.xml file located in <wildfly_home>/standalone/configuration has been replaced by standalone-full.xml. You need that in order to run the MDB's (Message driven beans).

In standalone.xml make sure your resources are correctly configured.   
Also make sure you download the right adapter for your JBoss/Wildfly configuration.  
I am currentlyt using the following:

1. [activemq-rar-5.10.0.rar](https://search.maven.org/search?q=a:activemq-rar) | [direct download](https://search.maven.org/remotecontent?filepath=org/apache/activemq/activemq-rar/5.15.9/activemq-rar-5.15.9.rar)
2. [Wildfly Server 16.0.0 Final](https://wildfly.org/downloads) | [direct download](https://download.jboss.org/wildfly/16.0.0.Final/wildfly-16.0.0.Final.zip)

Remember to run your wilfly version with  **--add-modules=java.se** as a VM switch.

Remove this from your standalone xml:

```xml
    <default-bindings context-service="java:jboss/ee/concurrency/context/default" datasource="java:jboss/datasources/ExampleDS" jms-connection-factory="java:jboss/DefaultJMSConnectionFactory" managed-executor-service="java:jboss/ee/concurrency/executor/default" managed-scheduled-executor-service="java:jboss/ee/concurrency/scheduler/default" managed-thread-factory="java:jboss/ee/concurrency/factory/default"/>

```
> standalone.xml (after overwritten by that all file)  
```xml
 <subsystem xmlns="urn:jboss:domain:resource-adapters:5.0">
    <resource-adapters>
        <resource-adapter id="activemq">
            <archive>
                activemq-rar-5.15.9.rar
            </archive>
            <transaction-support>XATransaction</transaction-support>
            <config-property name="ServerUrl">
                tcp://localhost:61616
            </config-property>
            <config-property name="UserName">
                defaultUser
            </config-property>
            <config-property name="UseInboundSession">
                false
            </config-property>
            <config-property name="Password">
                defaultPassword
            </config-property>
            <connection-definitions>
                <connection-definition class-name="org.apache.activemq.ra.ActiveMQManagedConnectionFactory" jndi-name="java:/ConnectionFactory" enabled="true" pool-name="ConnectionFactory">
                    <xa-pool>
                        <min-pool-size>1</min-pool-size>
                        <max-pool-size>20</max-pool-size>
                        <prefill>false</prefill>
                        <is-same-rm-override>false</is-same-rm-override>
                    </xa-pool>
                </connection-definition>
            </connection-definitions>
            <admin-objects>
                <admin-object class-name="org.apache.activemq.command.ActiveMQQueue" jndi-name="topic/PasssengerTopic" use-java-context="true" pool-name="test_queue">
                    <config-property name="PhysicalName">
                        testQueue
                    </config-property>
                </admin-object>
            </admin-objects>
        </resource-adapter>
    </resource-adapters>
</subsystem>
```
Please note that in this project, it is the presence of the jboss-deployment-structure.xml that activates the connection between JBoss and the JMS queues and topics.

## Sources

-   [How to install RabbitMQ on Mac using Homebrew](https://www.dyclassroom.com/howto-mac/how-to-install-rabbitmq-on-mac-using-homebrew)
-   [Error when deploying an .ear file containing an MDB to JBoss](https://stackoverflow.com/questions/15670322/error-when-deploying-an-ear-file-containing-an-mdb-to-jboss)
-   [JNDI adaptor for RabbitMQ integration in WildFly](https://github.com/isis2304/rabbitmq-wildfly-adaptor)
-   [40 Introduction to the Java Persistence API](https://javaee.github.io/tutorial/persistence-intro.html)
-   [How to run Wildfly 14 with java 11?](https://stackoverflow.com/questions/52852192/how-to-run-wildfly-14-with-java-11)
-   [Can not finish schema update: org.h2.jdbc.JdbcSQLException: Table & ldquo; PG_CLASS & rdquo; not found; SQL statement](https://www.codesd.com/item/can-not-finish-schema-update-org-h2-jdbc-jdbcsqlexception-table-pg-class-not-found-sql-statement.html)
-   [Could not complete schema update: org.h2.jdbc.JdbcSQLException: Table “PG_CLASS” not found; SQL statement](https://stackoverflow.com/questions/27694783/could-not-complete-schema-update-org-h2-jdbc-jdbcsqlexception-table-pg-class)

# Deprecated

## RabbitMQ Installation

There are some quarks while installing RabbitMQ on a MAC. Here is what I had to do:

$ brew install rabbitmq

Error: The `brew link` step did not complete successfully
The formula built, but is not symlinked into /usr/local
Could not symlink sbin/cuttlefish
/usr/local/sbin is not writable.

You can try again using:
  brew link rabbitmq
  
$ brew link rabbitmq

Linking /usr/local/Cellar/rabbitmq/3.7.15...
Error: Could not symlink sbin/cuttlefish
/usr/local/sbin is not writable.

$ sudo echo /usr/local/sbin >> /etc/paths.d/usr_local_sbin

$ sudo mkdir /usr/local/sbin

$ sudo chown <your_user_name>:admin /usr/local/sbin

$ brew link rabbitmq

## Rabbit MQ start

$ rabbitmq-server

## Rabbit MQ stop

## Rabbit MQ management

> Current exchanges

$ rabbitmqctl list_exchanges

> Current bindings

$ rabbitmqctl list_bindings

## References

-   [How To Unit Test JMS Code](https://activemq.apache.org/how-to-unit-test-jms-code)
-   [How To Test Your Distributed Message-Driven Application With Spring? by Pilo Software Developer](http://pillopl.github.io/testing-messaging/)
## About me 👨🏽‍💻🚀

[![Twitter Follow](https://img.shields.io/twitter/follow/joaofse?label=João%20Esperancinha&style=social)](https://twitter.com/joaofse)
[![GitHub followers](https://img.shields.io/github/followers/jesperancinha.svg?label=jesperancinha&style=social)](https://github.com/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Articles&message=On%20Medium&color=purple)](https://medium.com/@jofisaes)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Articles&message=On%20The%20Web&color=purple)](https://github.com/jesperancinha/project-signer/blob/master/project-signer-templates/LossArticles.md)
[![Generic badge](https://img.shields.io/static/v1.svg?label=BitBucket&message=jesperancinha&color=navy)](https://bitbucket.org/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=GitLab&message=jesperancinha&color=navy)](https://gitlab.com/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Homepage&message=joaofilipesabinoesperancinha.nl&color=6495ED)](http://joaofilipesabinoesperancinha.nl)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Homepage&message=Time%20Disruption%20Studios&color=6495ED)](http://tds.joaofilipesabinoesperancinha.nl/)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Homepage&message=Image%20Train%20Filters&color=6495ED)](http://itf.joaofilipesabinoesperancinha.nl/)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Homepage&message=MancalaJE&color=6495ED)](http://mancalaje.joaofilipesabinoesperancinha.nl/)
[![Generic badge](https://img.shields.io/static/v1.svg?label=DEV&message=Profile&color=green)](https://dev.to/jofisaes)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Medium&message=@jofisaes&color=green)](https://medium.com/@jofisaes)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Hackernoon&message=@jesperancinha&color=green)](https://hackernoon.com/@jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Free%20Code%20Camp&message=jofisaes&color=008000)](https://www.freecodecamp.org/jofisaes)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Hackerrank&message=jofisaes&color=008000)](https://www.hackerrank.com/jofisaes)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Code%20Forces&message=jesperancinha&color=008000)](https://codeforces.com/profile/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Coder%20Byte&message=jesperancinha&color=008000)](https://coderbyte.com/profile/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Code%20Wars&message=jesperancinha&color=008000)](https://www.codewars.com/users/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Acclaim%20Badges&message=joao-esperancinha&color=red)](https://www.youracclaim.com/users/joao-esperancinha/badges)
[![Generic badge](https://img.shields.io/static/v1.svg?label=All%20Badges&message=Badges&color=red)](https://github.com/jesperancinha/project-signer/blob/master/project-signer-templates/Badges.md)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Status&message=Project%20Status&color=red)](https://github.com/jesperancinha/project-signer/blob/master/project-signer-templates/Status.md)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Google%20Apps&message=Joao+Filipe+Sabino+Esperancinha&color=orange)](https://play.google.com/store/apps/developer?id=Joao+Filipe+Sabino+Esperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Code%20Pen&message=jesperancinha&color=orange)](https://codepen.io/jesperancinha)
[![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=ITF%20Chartizate%20Android&color=yellow)](https://github.com/JEsperancinhaOrg/itf-chartizate-android)
[![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=ITF%20Chartizate%20Java&color=yellow)](https://github.com/JEsperancinhaOrg/itf-chartizate-modules/tree/master/itf-chartizate-java)
[![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=ITF%20Chartizate%20API&color=yellow)](https://github.com/JEsperancinhaOrg/itf-chartizate/tree/master/itf-chartizate-api)
[![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=Markdowner%20Core&color=yellow)](https://github.com/jesperancinha/markdowner/tree/master/markdowner-core)
[![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=Markdowner%20Filter&color=yellow)](https://github.com/jesperancinha/markdowner/tree/master/markdowner-filter)
[![Generic badge](https://img.shields.io/static/v1.svg?label=Docker%20Images&message=jesperanciha&color=099CEC)](https://github.com/jesperancinha/project-signer/blob/master/project-signer-templates/DockerImages.md)