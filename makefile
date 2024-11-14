# Java Compiler
JAVAC = javac
JAR = jar
JAVA = java

# Folders
SRC = src
BIN = bin
JAR_FILE = gatorTicketMaster.jar
MAIN_CLASS = GatorTicketMaster

# Default target: compile and create .jar
all: jar

# Target to compile all .java files in src and output to bin directory
$(BIN)/%.class: $(SRC)/%.java
	@mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) $(SRC)/*.java

# Target to create the executable .jar file
jar: $(BIN)/$(MAIN_CLASS).class
	@echo "Main-Class: $(MAIN_CLASS)" > manifest.txt
	$(JAR) cfm $(JAR_FILE) manifest.txt -C $(BIN) .
	rm manifest.txt

# Target to run the .jar file
run-jar: jar
	$(JAVA) -jar $(JAR_FILE)

# Clean up compiled .class files and .jar file
clean:
	rm -rf $(BIN)/*.class $(JAR_FILE)

.PHONY: all jar run-jar clean
