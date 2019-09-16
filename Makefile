all: run

test:
	lein test

# export _JAVA_OPTIONS='-Dawt.useSystemAAFontSettings=lcd'
run: target/uberjar/gui-0.1.0-SNAPSHOT-standalone.jar
	java \
		-Dswing.aatext=true \
		-Dawt.useSystemAAFontSettings=lcd \
		-jar ./target/uberjar/gui-0.1.0-SNAPSHOT-standalone.jar

target/uberjar/gui-1.0.0-SNAPSHOT-standalone.jar:
	lein deps
	lein uberjar

.PHONY: test
