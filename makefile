# https://unix.stackexchange.com/questions/235223/makefile-include-env-file
#include classpath.env
#export $(shell sed 's/=.*//' classpath.env)


.PHONY: test
test: SHELL:=/bin/bash
test: compile
	java -cp classes:lib/junit-platform-console-standalone-1.9.0.jar org.junit.platform.console.ConsoleLauncher  --class-path build  --scan-class-path
clean:
	rm -R build

compile:
	find mnkgame -name "*.java" > sources.txt
	find test -name "*.java" >> sources.txt
	javac -cp lib/junit-jupiter-api-5.9.0-RC1.jar @sources.txt -d build

lint:
	find . -name "*.java" -exec clang-format {} \;

format:
	find . -name "*.java" -exec clang-format -i {} \;
