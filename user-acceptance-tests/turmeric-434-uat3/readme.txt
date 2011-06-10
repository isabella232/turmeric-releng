Test environment:

the tests cases in the test-automation/UAT434Test.java file calls the
2 version of the services described in the jira:
https://www.ebayopensource.org/jira/browse/TURMERIC-434

To run the tests, you will need installed a mysql instance in your local setup and the monitoring console. Check here for instructions:
http://ebayopensource.org/wiki/display/TURMERICDOC/Monitoring+Console+Installation+Guide

The tests do LOCAL mode calls to the 2 version of the MessageService. to
run this test do the following:
1.- Open a console and go to this folder (user-acceptance-tests/turmeric-434-uat3)
2.- Run: mvn clean install
3.- The build should run sucessfully 

Note: in the callServiceConsumerV2Test() test case, the call to the getMessages of the version 2 of the MessageService impl does a call to the version 1 of the MessageService.


For the REMOTE test, you need to install a web container. Jetty is the one used for this test.
You can check this documentation http://ebayopensource.org/wiki/display/TURMERICDOC/Runtime+Installation+Guide
as reference on Jetty setup.
Also, you will need SOAP UI installed to use the example projects sued to test this. Information on how to install/use SOAP UI can be found here:
http://ebayopensource.org/wiki/display/TURMERICDOC/Runtime+Installation+Guide

To run the Remote version of the test:
1.- in the turmeric-434-uat3/ folder, run: mvn clean install
2.- then, go to the turmeric-434-uat3/UAT3-WAR/target/ folder and get the uat3-war-0.9.0.4-Beta-SNAPSHOT.war in there
3.- Go to your <jetty root folder>/webapps and create a folder called: uat3
4.- Extract the contents of the war in the uat3 folder
5.- Start jetty
6.- Open a browser and go to this url: http://localhost:8080/uat3/UatMessageServiceV1?WSDL. This should get you the MessageServiceV1 wsdl
7.- Open a browser and go to this url: http://localhost:8080/uat3/UatMessageServiceV2?WSDL. This should get you the MessageServiceV2 wsdl
8.- Start SOAP UI
9.- In the main window of SOAP UI, import the following project files:
	- turmeric-434-uat3/UAT3-WAR/src/main/resources/Uat3RemoteTests-MessageServiceV1-soapui-project.xml and
	- turmeric-434-uat3/UAT3-WAR/src/main/resources/Uat3RemoteTests-MessageServiceV2-soapui-project.xml
10.- Run the TestMessageServiceV1 and TestMessageServiceV2 tests cases
11.- You should get successful responses

