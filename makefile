# Makefile for compiling Java files from src to the current directory

# Variables
SRC_DIR = src
OUT_DIR = out
MAIN_CLASS = GatorTicketMaster

# Target to compile all Java files
build:
	javac -d $(OUT_DIR) $(SRC_DIR)/*.java

# Target to run the Java program with the specified text file
run: build
	@if [ -z "$(file)" ]; then \
		echo "Error: No file name specified. Halting execution."; \
		exit 1; \
	else \
		echo "Running with file name: $(file)"; \
		java -cp $(OUT_DIR) $(MAIN_CLASS) $(file); \
	fi

# Target to clean up compiled files
clean:
	rm -rf $(OUT_DIR)

# Default target (compile Java files)
all: build
