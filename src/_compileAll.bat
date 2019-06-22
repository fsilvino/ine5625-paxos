d:

cd D:\eclipse-workspace\ine5625-paxos\src

del common\*.class
del client\*.class
del idgenerator\*.class
del learner\*.class
del acceptor\*.class
del proposer\*.class
javac common\*.java client\*.java idgenerator\*.java learner\*.java acceptor\*.java proposer\*.java

pause