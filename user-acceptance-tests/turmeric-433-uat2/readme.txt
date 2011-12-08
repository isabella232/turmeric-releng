Test environment:

the SOAPUI test project turmeric-433-uat2/Turmeric-433-soapui-project.xml are meant to run 
manually from SOAPUI.

To run the tests, you will need to install the Monitoing Console: https://www.ebayopensource.org/wiki/display/TURMERICDOC/Monitoring+Console+Installation+Guide


Steps:
1.- Start the mysql instance
2.- unzip the turmericdbt433.sql.zip file
3.- load the sql script turmericdbt433.sql in mysql, using this command: mysql -u root -p turmericdb < turmericdbt433.sql
4.- in the turmeric-433-uat2/ folder run: mvn clean install
5.- Build should succeed
6.- grab the turmeric-433-uat2-0.9.0.4-Beta-SNAPSHOT.war file from the turmeric-433-uat2/UAT2-WAR/target/ folder.
7.- Uncompress the war in the <jetty root>webapps/uat2 folder.
8.- Extract the mysql-connector-java-5.1.13.jar inside the uat2/WEB-INF/lib folder and copy it to <jetty-turmeric home/lib/jdbc. This is needed to make the mysql driver available to runtime.
9.- Open the file <jetty-turmeric home>/resources/META-INF/security/config/AuthenticationPolicy.xml and add the following content:

<resource name="Uat2MessageServiceV1" default-authentication-method="basic" type="SERVICE">
    <operation name="getVersion">
      <authentication-method>basic</authentication-method>
    </operation>
    <operation name="sendMesssage">
      <authentication-method>basic</authentication-method>
    </operation>
    <operation name="getMesssage">
      <authentication-method>basic</authentication-method>
    </operation>
  </resource>
  
 <resource name="PolicyEnforcementService" default-authentication-method="basic" type="SERVICE">
    <operation name="verifyAccess">
      <authentication-method>basic</authentication-method>
    </operation>
  </resource>

10.- Open the file <jetty-turmeric home>/resources/META-INF/soa/services/config/GlobalServiceConfig.xml and uncomment the following blocks:

<monitor-config>
        <storage-provider name="DAOMetricsStorageProvider">
            <class-name>org.ebayopensource.turmeric.monitoring.storage.DAOMetricsStorageProvider</class-name>
            <storage-options>
                <option name="persistenceUnitName">metrics</option>
                <option name="metricsDAOClassName">org.ebayopensource.turmeric.monitoring.MetricsDAOImpl</option>
                <option name="storeServiceMetrics">false</option>
            </storage-options>
        </storage-provider>
        <snapshot-interval>60</snapshot-interval>
    </monitor-config>

11.- Open the file <jetty-turmeric home>/resources/META-INF/persistence.xml and change the properties in all the persistence-unit elements to point to mysql.
The properties for mysql are commented, just uncomment them and comment the ones setup for apache derby.


12.- Start jetty
13.- You should be able to access the url: http://localhost:8080/uat2/Uat2MessageServiceV1?WSDL
14.- Open SOAP UI
15.- Open the test project found in: turmeric-433-uat2/Turmeric-433-soapui-project.xml
16.- Run the TestSuite-433. It should succeed

NOTE: To view the metrics, access the monitoring console
