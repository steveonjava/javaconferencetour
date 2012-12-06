package javatour;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    WebEngine engine;
    TextField latField = new TextField();
    TextField lngField = new TextField();

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createMap());
        scene.setFill(Color.rgb(230, 230, 230));
        stage.setScene(scene);
        stage.setTitle("Java Conference Tour");
        stage.setHeight(550);
        stage.setWidth(700);
        stage.show();
    }

    private BorderPane createMap() {
        BorderPane inner = new BorderPane();
        inner.setTop(createLatLng());
        inner.setCenter(createWebView());
        inner.setLeft(createConferences());
        BorderPane outer = new BorderPane();
        AnchorPane anchor = new AnchorPane();
        anchor.getChildren().add(inner);
        AnchorPane.setBottomAnchor(inner, 100d);
        AnchorPane.setTopAnchor(inner, 30d);
        AnchorPane.setLeftAnchor(inner, 30d);
        AnchorPane.setRightAnchor(inner, 30d);
        outer.setCenter(anchor);
        return outer;
    }
    
    private HBox createLatLng() {
        HBox hbox = new HBox();
        hbox.getChildren().addAll(new Label("Latitude:"), latField, new Label("Longitude:"), lngField);
        latField.setEditable(false);
        lngField.setEditable(false);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(0, 0, 10, 0));
        return hbox;
    }

    private WebView createWebView() {
        WebView webView = new WebView();
        engine = webView.getEngine();
        engine.load(getClass().getResource("content.html").toString());
        engine.getLoadWorker().stateProperty().addListener(
        new ChangeListener<State>() {
            public void changed(ObservableValue ov, State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("recenter", new Recenter());
                }
            }
        });
        webView.setEffect(new Reflection(10, .5, .75, 0));
        return webView;
    }

    private Accordion createConferences() {
        Accordion accordion = new Accordion();
        final ConferencePane sfo = createConference("JavaOne San Francisco", 37.775057, -122.416534, "http://steveonjava.com/wp-content/uploads/2010/07/JavaOne-2010-Speaker-e1348687255736.png");
        accordion.getPanes().add(sfo);
        accordion.getPanes().add(createConference("J-Fall", 52.219913, 5.474253, "http://steveonjava.com/wp-content/uploads/2011/11/jfall3.png"));
        accordion.getPanes().add(createConference("Devoxx", 51.206883, 4.44, "http://steveonjava.com/wp-content/uploads/2010/07/LogoDevoxxNeg150.png"));
        accordion.getPanes().add(createConference("JavaOne Latin America", -23.548943,-46.638818, "http://steveonjava.com/wp-content/uploads/2012/09/javaone-latinamerica.png"));
        accordion.getPanes().add(createConference("GeeCON", 50.064633, 19.949799, "http://steveonjava.com/wp-content/uploads/2011/03/geecon.png"));
        accordion.getPanes().add(createConference("Jazoon", 47.382079, 8.528137, "http://steveonjava.com/wp-content/uploads/2010/04/jazoon.png"));
        accordion.getPanes().add(createConference("OSCON", 45.515008, -122.693253, "http://steveonjava.com/wp-content/uploads/2011/05/oscon.png"));
        sfo.setExpanded(true);
        accordion.setExpandedPane(sfo);
        latField.setText(String.valueOf(sfo.lat));
        lngField.setText(String.valueOf(sfo.lng));
        accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
            public void changed(ObservableValue<? extends  TitledPane> ov, TitledPane t, TitledPane t1) {
                if (t1 != null) {
                    ((ConferencePane)t1).navigateTo();
                }
            }
        });
        return accordion;
    }

    private ConferencePane createConference(String name, final double lat, final double lon, String imageUrl) {
        return new ConferencePane(name, new ImageView(new Image(imageUrl)), lat, lon);
    }

    public class Recenter {
        public void latLng(String lat, String lng) {
            latField.setText(lat);
            lngField.setText(lng);
        }
    }

    public class ConferencePane extends TitledPane {
        public final double lat;
        public final double lng;

        private ConferencePane(String label, Node node, double lat, double lng) {
            super(label, node);
            this.lat = lat;
            this.lng = lng;
        }
        
        public void navigateTo() {
            engine.executeScript("moveMap(" + lat + ", " + lng + ");");
        }
    }
}
