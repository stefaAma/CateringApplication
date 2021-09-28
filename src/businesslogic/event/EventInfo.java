package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.user.User;
import businesslogic.user.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventInfo implements EventItemInfo {
    private int id;
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private int participants;
    private User organizer;
    private User chef;
    private ObservableList<ServiceInfo> services;

    private static ObservableList<EventInfo> events = FXCollections.observableArrayList();
    private static ObservableList<EventInfo> chefEvents = FXCollections.observableArrayList();

    public EventInfo(String name) {
        this.name = name;
        id = 0;
    }

    public ObservableList<ServiceInfo> getServices() {
        return FXCollections.unmodifiableObservableList(this.services);
    }

    public String toString() {
        String eventInfo = name + ": " + dateStart + "-" + dateEnd + ", " + participants + " pp. organizer(" + organizer.getUserName() + ")";
        if (chef == null)
            return eventInfo;
        else
            return eventInfo + " chef (" + chef.getUserName() + ")";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<EventInfo> loadAllEventInfo() {
        if (events.size() > 0)
            return events;
        String query = "SELECT * FROM Events WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                EventInfo e = new EventInfo(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("date_start");
                e.dateEnd = rs.getDate("date_end");
                e.participants = rs.getInt("expected_participants");
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                int chef = rs.getInt("chef_id");
                e.chef = User.loadUserById(chef);
                events.add(e);
            }
        });

        for (EventInfo e : events) {
            e.services = ServiceInfo.loadServiceInfoForEvent(e.id);
        }
        return events;
    }

    public static ObservableList<EventInfo> loadChefEvents() {
        if (events.size() == 0)
            loadAllEventInfo();
        chefEvents.clear();
        for (EventInfo eventInfo : events) {
            if (eventInfo.isOwnedByChef())
                chefEvents.add(eventInfo);
        }
        return chefEvents;
    }

    public boolean isOwnedByChef() {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (user != null && chef != null)
            return chef.getId() == user.getId();
        return false;
    }

    public String getName() {
        return name;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public int getParticipants() {
        return participants;
    }

    public User getOrganizer() {
        return organizer;
    }

}
