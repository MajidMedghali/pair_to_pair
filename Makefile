.PHONY: all pair tracker recette interface build_tracker_test build_pair_test test run_pair_tests run

all: pair tracker  build build_pair_test build_tracker_test 


pair:
	javac -Xdiags:verbose -d build -cp build src/pair/*.java

tracker:
	gcc src/tracker/tracker.c src/tracker/config_parser.c src/tracker/thpool.c  src/tracker/command_parser.c -o tracker

interface:
	java -ea -cp build/ WebPagePanel

generate_files:
	./src/files_generator.sh 


encode_file:
	./src/copy_text.sh ./res/txt/1_10MO_files.txt 
	./src/copy_text.sh ./res/txt/2_10MO_files.txt 
	./src/copy_text.sh ./res/txt/3_10MO_files.txt 
	./src/copy_text.sh ./res/txt/4_10MO_files.txt 

	./src/encode_in_bin.sh  ./res/txt/1_10MO_files.txt ./res/bin/1_10MO_files.bin
	./src/encode_in_bin.sh  ./res/txt/2_10MO_files.txt ./res/bin/2_10MO_files.bin
	./src/encode_in_bin.sh  ./res/txt/3_10MO_files.txt ./res/bin/3_10MO_files.bin
	./src/encode_in_bin.sh  ./res/t	xt/4_10MO_files.txt ./res/bin/4_10MO_files.bin


build_tracker_test:
	gcc ./tst/tracker/main.c  ./src/tracker/command_parser.c -o test


recette:
	java -ea -cp build/ Execution
run:
	./tracker
test:
	./test

run_pair_tests:
	java -ea -cp build/ PairTest
build_pair_test:
	javac -Xdiags:verbose -d build -cp build tst/pair/*.java

clean:
	rm -r tracker  *.class  build/* test res
