Test environment:

The junit test in test-automation/ (UAT432Test.java) test the LOCAL asynch and synch
mode of the tests.

For the REMOTE test, you need to install the jetty-turmeric distribution: 
https://www.ebayopensource.org/wiki/display/TURMERICDOC/Distribution+-+Jetty+Turmeric+-+Installation+Guide

Steps to run the Test:
1.- step in the turmeric-432-uat1/ and run: mvn clean install. This should fire the UAT432Test in test-automation
2. -The Build should succeed
2.1.- You also can run the  UAT432Test.java in eclipse as a standard Junit test, if you do, 
you should see the following in the standard output:
Hello, World
receving asynch message :Hello, Asynch World

To run the Remote version of the test:
1.- in the turmeric-432-uat1/ folder, run: mvn clean install
2.- then, go to the turmeric-432-uat1/UAT1-WAR/target/ folder and get the uat1-war-*.war in there
3.- Go to your <jetty-turmeric root folder>/webapps and create a folder called: uat1
4.- Extract the contents of the war in the uat1 folder
5.- Start jetty-turmeric
6.- Open a browser and go to this url: http://localhost:8080/uat1/UatHelloWorldV1?WSDL. This should get you the Hello World wsdl
7.- Open in eclipse the class: turmeric-432-uat1/UatHelloWorldV1Consumer/src/org/ebayopensource/turmeric/uat1/v1/services/helloworld/consumer/HelloWorldV1Consumer.java
8.- Run the class with the "Run as Java Application" in Eclipse
9.- In the standard output should be seen the following messages:
Hello, World
receving asynch message :Hello, Asynch World
