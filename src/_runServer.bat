d:

cd D:\eclipse-workspace\ine5625-paxos\src

start /min rmiregistry

timeout 5

start /min run.bat security.policy idgenerator.IDGeneratorImpl

start /min run.bat security.policy proposer.ProposerImpl proposer1.xml
start /min run.bat security.policy proposer.ProposerImpl proposer2.xml

start /min run.bat security.policy acceptor.AcceptorImpl acceptor1.xml
start /min run.bat security.policy acceptor.AcceptorImpl acceptor2.xml
start /min run.bat security.policy acceptor.AcceptorImpl acceptor3.xml

start /min run.bat security.policy learner.LearnerImpl learner1.xml