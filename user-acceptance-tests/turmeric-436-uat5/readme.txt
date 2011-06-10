Test environment:

For the test, you need to install:
1.- maven 3.0
2.- Wso2 Governance Registry. See this link to install wso2:
http://ebayopensource.org/wiki/display/TURMERICDOC/Repository+Service+Installation+Guide

NOTE: Install the 3.5.x version of the wso2 registry server, as there's an issue with the 3.6 version. Don't download the turmeric-repository-dist for this test. Just do the steps related to the wso2 server setup and the jetty related setup.
NOTE #2: the step #4 in the Repository install guide mentions the lifecycle to use. For this test, you need to use the lifecycle in the file: turmeric-436-uat5/TurmericLifeCycle.xml. 

Steps to run the Test:
1.- Start the wso2 instance
1.- go to the turmeric-436-uat5/ folder
2.- run: mvn clean install
3.- The build should succeed 
6.- Go to the turmeric-436-uat5/UAT5-WAR/target/ folder and get the uat5-war-0.9.1.0-SNAPSHOT.war in there
3.- Go to your <jetty root folder>/webapps and create a folder called: uat5
4.- Extract the contents of the war in the uat5 folder
4.a.- if there is a ConfigRoot/ in the <jetty root> folder, remove it
5.- Start jetty
6.- Open in SOAP UI the test project here: turmeric-436-uat5/Turmeric-436-soapui-project.xml 
7.- Run the t-436 test suite. It should go green
8.- You should be able to log in the wso2 admin UI. In there, you should find the service in this path: /_system/governance/services/http/www/ebayopensource/org/turmeric/uat1/v1/services/HelloWorld. The service should have Approved status
9.- Select the check "Submitted for Promotion" in the lifecycle box (right side of the screen). Click on "promote". The service should be now have "Deployed" status
10.- Again, select the check and click on "promote". The service should be in "Retired" status.



