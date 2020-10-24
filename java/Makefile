all: aco

aco:
	mkdir ./build
	javac -d ./build *.java -Xlint:unchecked
	cp ./layout.fxml ./build/aco/layout.fxml
	cd ./build; jar -cvfm ./aco.jar ../META-INF/MANIFEST.MF ./aco/*

run:
	java -jar ./build/aco.jar

clean:
	rm -rf ./build