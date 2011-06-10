Test environment:

the SOAPUI test project turmeric-433-uat2/Turmeric-433-soapui-project.xml are meant to run 
manually from SOAPUI.

To run the tests, you will need installed a mysql instance in your local setup and the monitoring console, and a web server such as jetty. Check here for instructions:
http://ebayopensource.org/wiki/display/TURMERICDOC/Monitoring+Console+Installation+Guide
Important note: Do all the tests required in that guide, *EXCEPT* the loading of the sql script turmericdb_setup_script.sql. The script provided for the test have the neccesary data.


Steps:
1.- Start the mysql instance
2.- unzip the turmericdbt433.sql.zip file
3.- load the sql script turmericdbt433.sql in mysql, using this command: mysql -u root -p turmericdb < turmericdbt433.sql
4.- in the turmeric-433-uat2/ folder run: mvn clean install
5.- Build should succeed
6.- grab the turmeric-433-uat2-0.9.0.4-Beta-SNAPSHOT.war file from the turmeric-433-uat2/UAT2-WAR/target/ folder.
7.- Uncompress the war in the <jetty root>webapps/uat2 folder.
8.- Start jetty
9.- You should be able to access the url: http://localhost:8080/uat2/Uat2MessageServiceV1?WSDL
10.- Open SOAP UI
11.- Open the test project found in: turmeric-433-uat2/Turmeric-433-soapui-project.xml
12.- Run the TestSuite-433. It should succeed

NOTE: To view the metrics, access the monitoring console
