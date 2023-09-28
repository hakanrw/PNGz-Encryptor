package tk.candarlabs.pngzapp;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import tk.candarlabs.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Function;


class UIFunctions {
    static Scene scene = null;
    static boolean arrowIsBlue = false;
    final static String DEFAULT_FILE_IMG = UIFunctions.class.getClassLoader().getResource("img/file.png").toString();
    static HostServices hostServices = null;

    static void makeButton(Pane button, Function<Void, Void> pressFunc){
        Function<Pane, Void> overFunc = (pane) -> {
            pane.setOpacity(0.3);
            pane.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0))));
            return null;
        };
        Function<Pane, Void> outFunc = (pane) -> {
            pane.setBackground(null);
            pane.setOpacity(1.0);
            return null;
        };
        button.setOnMouseEntered(mouseEvent -> overFunc.apply(button));
        button.setOnMouseExited(mouseEvent -> outFunc.apply(button));
        button.setOnMouseClicked(mouseEvent -> pressFunc.apply(null));
    }

    static void setInputImage(Image image){
        ImageView inputImage = (ImageView) scene.lookup("#imginput");
        inputImage.setImage(image);
    }

    static void clearOutputFields(){
        ImageView outputImage = (ImageView) scene.lookup("#imgoutput");
        outputImage.setImage(null);
        ((Label)scene.lookup("#outname")).setText("");
    }

    static void setOutputImage(Image image){
        ImageView outputImage = (ImageView) scene.lookup("#imgoutput");
        outputImage.setImage(image);
    }

    static void setArrowBlue(String text){
        DropShadow dp = new DropShadow();
        dp.setWidth(21);
        dp.setHeight(21);
        dp.setRadius(10);
        dp.setOffsetX(1);
        dp.setOffsetY(1);
        dp.setColor(Color.valueOf("#2c8eff"));

        ImageView image = (ImageView) scene.lookup("#arrow");
        image.setEffect(dp);
        image.setImage(new Image(UIFunctions.class.getClassLoader().getResource("img/arrow.png").toString()));

        ((Label) scene.lookup("#arrowtxt")).setText(text);
        arrowIsBlue = true;
    }

    static void setArrowGray(){
        ImageView image = (ImageView) scene.lookup("#arrow");
        image.setEffect(null);
        image.setImage(new Image(UIFunctions.class.getClassLoader().getResource("img/arrow-gray.png").toString()));

        ((Label) scene.lookup("#arrowtxt")).setText("Drop a File");
        arrowIsBlue = false;
    }

    static void init(){
        // Arrow defaults to gray
        setArrowGray();

        // Set links
        Pane webButton = (Pane) scene.lookup("#webbutton");
        Pane aboutButton = (Pane) scene.lookup("#aboutbutton");
        makeButton(webButton, aVoid -> {
            hostServices.showDocument("https://hakan.candar.dev");
            return null;
        });
        makeButton(aboutButton, aVoid -> {
            hostServices.showDocument("https://github.com/hakanrw/PNGZ-Encryptor");
            return null;
        });

        // Set drag & drop
        AnchorPane mainBar = (AnchorPane)  scene.lookup("#mainbar");
        AnchorPane inputPane = (AnchorPane) scene.lookup("#paneinput");

        mainBar.setOnDragOver(dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.ANY);
            dragEvent.consume();
        });
        mainBar.setOnDragEntered(dragEvent -> {
            inputPane.getStyleClass().add("drop");
            scene.lookup("#imgdrop").setVisible(true);
            scene.lookup("#imginput").setVisible(false);
            dragEvent.consume();
        });
        mainBar.setOnDragExited(dragEvent -> {
            inputPane.getStyleClass().remove("drop");
            scene.lookup("#imgdrop").setVisible(false);
            scene.lookup("#imginput").setVisible(true);
            dragEvent.consume();
        });
        mainBar.setOnDragDropped(dragEvent -> {
            if(!dragEvent.getDragboard().hasFiles())
                return;
            File dragged = dragEvent.getDragboard().getFiles().get(0);
            EncryptorFunctions.setFile(dragged);

            Image defaultImage = new Image(DEFAULT_FILE_IMG);
            if(EncryptorFunctions.operation.equals("Encode"))
                setInputImage(defaultImage);
            else {
                try {
                    setInputImage(SwingFXUtils.toFXImage(Utils.fileToImage(dragged), null));
                }catch (Exception e){
                    setInputImage(defaultImage);
                }
            }

            ((Label)scene.lookup("#inname")).setText(dragged.getName());
            clearOutputFields();
            dragEvent.consume();
        });

        // Set Arrow button
        ImageView arrow = (ImageView) scene.lookup("#arrow");
        Pane arrowButton = (Pane) scene.lookup("#arrowbutton");

        arrowButton.setOnMouseEntered(mouseEvent -> {
            if(arrowIsBlue)
                arrow.setOpacity(0.5);
        });
        arrowButton.setOnMouseExited(mouseEvent -> {
            if(arrowIsBlue)
                arrow.setOpacity(1.0);
        });
        arrowButton.setOnMouseClicked(mouseEvent -> {
            if(EncryptorFunctions.operation.equals(""))
                return;
            setArrowGray();
            ((Label) scene.lookup("#arrowtxt")).setText("Please Wait..");

            Thread t = new Thread(() -> {
                EncryptorFunctions.applyOperation();
                String inFileName = EncryptorFunctions.appliedFile.getName();
                String fileName = inFileName.substring(0, inFileName.lastIndexOf("."));
                if(EncryptorFunctions.operation.equals("Encode")){
                    fileName += ".png";
                    Platform.runLater(() -> {
                        setOutputImage(SwingFXUtils.toFXImage(EncryptorFunctions.encoder.getEncodedImage(), null));
                    });
                }
                else {
                    fileName +=  "." + EncryptorFunctions.decoder.getDecodedFile().extension;
                    Platform.runLater(() -> {
                        setOutputImage(new Image(DEFAULT_FILE_IMG));
                    });
                }
                String finalFileName = fileName;
                Platform.runLater(() -> {
                    setArrowBlue(EncryptorFunctions.operation);
                    ((Label)scene.lookup("#outname")).setText(finalFileName);
                });

            });
            t.start();
        });
    }
}
