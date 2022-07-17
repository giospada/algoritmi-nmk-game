.PHONY: test
test: SHELL:=/bin/bash
test:
	source classpath.env
	javac ./test/*.java
	java -jar test/junit-platform-console-standalone-1.7.2.jar --class-path test --scan-class-path