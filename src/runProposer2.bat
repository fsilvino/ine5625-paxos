start /min rmiregistry
java -classpath D:\eclipse-workspace\ine5625-paxos\src -Djava.rmi.server.codebase=file:/D:/eclipse-workspace/ine5625-paxos/src/ -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy proposer.ProposerImpl proposer2.xml