to run:

in pom directory, to run using maven
    mvn clean
    mvn package -f pom_mvn.xml
    mvn javafx:run -f pom_mvn.xml


###### Ill succeed hopefully ahah
to run with jar:
see full command of eclipse run configurations
	compile pom.xml with 
	 mvn clean -Dmaven.clean.failOnError=false compile assembly:single -DskipTests=true
 run with 
	java -jar ...jar






###########

to compile command line:
	mvn  install -f pom_cl.xml
then go to target and do:
	java -jar rubiks_cl-1.0.jar LLFFUFRBBDDULFBLBDRDRURRRRLFRBRDFUBBUDFDBUDLLUUFLLFBUD

