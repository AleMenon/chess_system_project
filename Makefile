#Makefile for Java#

#Directories
SRC=src
BIN=bin

#Main class and code(entry point)
MAIN_CLASS=application.Program
MAIN_CODE=application/Program.java

#Compile and run the code (default when calling 'make')
all: compile run

compile: 
	@javac -d $(BIN) -sourcepath $(SRC) $(SRC)/$(MAIN_CODE)

run:
	@java -cp $(BIN) $(MAIN_CLASS)
