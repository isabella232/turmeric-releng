Test environment:

the tests cases in the test-automation/UAT434Test.java file calls the
2 version of the services described in the jira:
https://www.ebayopensource.org/jira/browse/TURMERIC-434

Prerequisites:

- To run the tests, you will need to install the TMC and jetty-turmeric: 
https://www.ebayopensource.org/wiki/display/TURMERICDOC/Monitoring+Console+Installation+Guide

- You also need mysql installed and running. You need to create a db called 'turmericdb' with owner 'turmeric', password 'turmeric'.

The tests do LOCAL mode calls to the 2 version of the MessageService. to
run this test do the following:
1.- Start the mysql instance if it's not already running
2.- Open a console and go to this folder (user-acceptance-tests/turmeric-434-uat3)
3.- Run: mvn clean install
4.- The build should run sucessfully 

Note: in the callServiceConsumerV2Test() test case, the call to the getMessages of the version 2 of the MessageService impl does a call to the version 1 of the MessageService.


For the REMOTE test, you need to run the jetty-turmeric server.

Also, you will need SOAP UI installed to use the example projects used to test this. Information on how to install/use SOAP UI can be found here:
http://ebayopensource.org/wiki/display/TURMERICDOC/Runtime+Installation+Guide

To run the Remote version of the test:
1.- in the turmeric-434-uat3/ folder, run: mvn clean install
2.- then, go to the turmeric-434-uat3/UAT3-WAR/target/ folder and get the uat3-war.war in there
3.- Go to your <jetty root folder>/webapps and create a folder called: uat3
4.- Extract the contents of the war in the uat3 folder
5.- Extract the mysql-connector-java-5.1.13.jar inside the uat3/WEB-INF/lib folder and copy it to <jetty-turmeric home/lib/jdbc. This is needed to make the mysql driver available to runtime.
6.- Start jetty
7.- Open a browser and go to this url: http://localhost:8080/uat3/UatMessageServiceV1?WSDL. This should get you the MessageServiceV1 wsdl
8.- Open a browser and go to this url: http://localhost:8080/uat3/UatMessageServiceV2?WSDL. This should get you the MessageServiceV2 wsdl
9.- Start SOAP UI
10.- In the main window of SOAP UI, import the following project files:
	- turmeric-434-uat3/UAT3-WAR/src/main/resources/Uat3RemoteTests-MessageServiceV1-soapui-project.xml and
	- turmeric-434-uat3/UAT3-WAR/src/main/resources/Uat3RemoteTests-MessageServiceV2-soapui-project.xml
11.- Run the TestMessageServiceV1 and TestMessageServiceV2 tests cases
12.- You should get successful responses

NOTE: You can go now to the TMC and check the metrics for both services: http://localhost:8080/console/

