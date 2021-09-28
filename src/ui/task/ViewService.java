package ui.task;

import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ViewService {

    @FXML
    private Text serviceName;

    @FXML
    private Text date;

    @FXML
    private Text timeStart;

    @FXML
    private Text timeEnd;

    @FXML
    private Text menuName;

    @FXML
    private  Text participants;

    public void initialize() {
        serviceName.setText("Nessun Servizio Selezionato!");
        date.setText("xxxx-xx-xx");
        timeStart.setText("xx:xx:xx");
        timeEnd.setText("xx:xx:xx");
        menuName.setText("Nessun Menu!");
        participants.setText("0");
    }

    public void viewServiceInfo(ServiceInfo service) {
        serviceName.setText(service.getName());
        date.setText(service.getDate().toString());
        timeStart.setText(service.getTimeStart().toString());
        timeEnd.setText(service.getTimeEnd().toString());
        participants.setText(String.valueOf(service.getParticipants()));
        Menu menu = service.getMenu();
        if (menu != null)
            menuName.setText(menu.getTitle());
        else
            menuName.setText("Nessun menu confermato per il servizio");
    }

}
