# https://unix.stackexchange.com/questions/235223/makefile-include-env-file
include classpath.env
export $(shell sed 's/=.*//' classpath.env)


.PHONY: test
test: SHELL:=/bin/bash
test:
	env | grep "CLASSPATH" # just to see if the env var is well setted
	javac ./**/*.java # need everything to make tests
	java -cp . -jar lib/junit-platform-console-standalone-1.7.2.jar --class-path . --scan-class-path
