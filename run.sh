source .env

bash compile.sh
bash build.sh

java --module-path "$JAVAFX" --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.media,javafx.swing -jar "dist/$PACKAGE.jar"