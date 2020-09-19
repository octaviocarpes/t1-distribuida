all:			GameClient.class GameServer.class \
			GameInterfaceClient.class GameInterfaceServer.class

GameInterfaceClient.class:	GameInterfaceClient.java
			@javac GameInterfaceClient.java

GameInterfaceServer.class:	GameInterfaceServer.java
			@javac GameInterfaceServer.java

GameClient.class:	GameClient.java
			@javac GameClient.java

GameServer.class:	GameServer.java
			@javac GameServer.java

clean:
			@rm -f *.class *~

