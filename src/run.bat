d:

cd D:\eclipse-workspace\ine5625-paxos\src

start /min rmiregistry

timeout 5

start /min runIDGenerator.bat

start /min runProposer1.bat
start /min runProposer2.bat

start /min runAcceptor1.bat
start /min runAcceptor2.bat
start /min runAcceptor3.bat

start /min runLearner1.bat