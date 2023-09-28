source .env
shopt -s globstar

[ -e out ] && rm -r out

javac -d out/classes --module-path "$JAVAFX" --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.media,javafx.swing -cp "lib/tk.candarlabs.pngz.jar" src/**/*.java

