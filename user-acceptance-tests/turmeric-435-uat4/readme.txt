Test environment:

The test environment setup in the turmeric-435-uat4/test-automation folder runs the soap ui
test project found here: turmeric-435-uat4/test-automation/src/test/resources/turmeric-435-soapui-tests.xml

For the test, you need to install:
1.- maven 3.0
2.- Wso2 Governance Registry. See this link to install wso2:
http://ebayopensource.org/wiki/display/TURMERICDOC/Repository+Service+Installation+Guide
NOTE: Install the 3.5.x version of the wso2 registry server, as there's an issue with the 3.6 version. Don't download the turmeric-repository-dist for this test. Just do the steps related to the wso2 server setup and the jetty related setup

Steps to run the Test:
1.- Start the wso2 instance
1.- go to the turmeric-435-uat4/ folder
1.a.- if there is a ConfigRoot/ in the turmeric-435-uat4/, remove it
2.- run: mvn clean install
3.- The build should succeed 
NOTE: After running the build, you can go to the wso2 admin ui to check the assets created, here:
https://localhost:9443/carbon/admin/login.jsp. user:admin/password:admin. Server url: https://localhost:9443/services/

Inside the wso2 admin ui, you can search the submitted wsdl in this path:
/_system/governance/services/http/www/ebayopensource/org/turmeric/uat1/v1/services/HelloWorld

And the Assertions stored in this path:
/_system/governance/turmeric435/lib

To run the manual version of the test:
Note: you need to have the wso2 server running before doing these steps.

1.- in the turmeric-435-uat4/ folder, run: mvn clean install
2.- then, go to the turmeric-435-uat4/UAT4-WAR/target/ folder and get the uat4-war-<build version>-SNAPSHOT.war in there.
3.- Go to your <jetty root folder>/webapps and create a folder called: uat4
4.- Extract the contents of the war in the uat4 folder
4.a.- if there is a ConfigRoot/ in the <jetty root> folder, remove it
5.- Start jetty
6.- Open in SOAP UI the test project here: turmeric-435-uat4/test-automation/src/test/resources/turmeric-435-soapui-tests.xml
7.- Run the t-435 test suite. It should go green
NOTE: You should be able to access the RS wsdl in this url: http://localhost:8080/uat4/TurmericRSV1?WSDL
and the AssertionsService in this url: http://localhost:8080/uat4/TurmericASV1?WSDL


