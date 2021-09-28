package businesslogic.task.turn;
import businesslogic.task.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

public class CookingTurn implements ShiftBoardInfo {
    private int id;
    private String date;
    private Time startTime;
    private Time endTime;
    private boolean full;
    private ObservableList<Task> tasks;

    private static Map<Integer, CookingTurn> loadedCookingTurns = new HashMap<>();

    /*private CookingTurn(int id, String date, int startHour, int startMinute, int endHour, int endMinute, boolean full, List<Task> tasks) {
        this.id = id;
        this.date = date;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.full = full;
        this.tasks = tasks;
    }*/

    public static int getTurnLength(CookingTurn cookingTurn) {
        Calendar timeStart = Calendar.getInstance();
        Calendar timeEnd = Calendar.getInstance();
        timeStart.setTime(cookingTurn.getStartTime());
        timeEnd.setTime(cookingTurn.getEndTime());
        int hourDiff = timeEnd.get(Calendar.HOUR_OF_DAY) - timeStart.get(Calendar.HOUR_OF_DAY);
        int minuteDiff = timeEnd.get(Calendar.MINUTE) - timeStart.get(Calendar.MINUTE);
        return  (hourDiff * 60) + minuteDiff;
    }

    public static CookingTurn loadCookingTurnById(int id) {
        if (loadedCookingTurns.containsKey(id))
            return loadedCookingTurns.get(id);
        String getTurnById = "SELECT * FROM CookingTurn where id = " + id + ";";
        PersistenceManager.executeQuery(getTurnById, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                CookingTurn cookingTurn = new CookingTurn();
                cookingTurn.id = id;
                loadedCookingTurns.put(id, cookingTurn);
                cookingTurn.date = rs.getString("date");
                cookingTurn.startTime = rs.getTime("start_time");
                cookingTurn.endTime = rs.getTime("end_time");
                cookingTurn.full = rs.getBoolean("saturation");
                String getTasks = "SELECT id FROM Task WHERE cooking_turn_id = " + id + ";";
                ObservableList<Task> tasks = FXCollections.observableArrayList();
                PersistenceManager.executeQuery(getTasks, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        tasks.add(Task.loadTaskById(rs.getInt("id")));
                    }
                });
                cookingTurn.tasks = tasks;

            }
        });
        return loadedCookingTurns.get(id);
    }

    public static ObservableList<CookingTurn> getCookingTurnByShiftBoard(int shiftBoardID) {
        ObservableList<CookingTurn> cookingTurns = FXCollections.observableArrayList();
        String getTurns = "SELECT id FROM CookingTurn WHERE shift_board_id = " + shiftBoardID + ";";
        PersistenceManager.executeQuery(getTurns, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                cookingTurns.add(loadCookingTurnById(rs.getInt("id")));
            }
        });
        return cookingTurns;
    }

    public static void cookingTurnFull(CookingTurn cookingTurn) {
        String updateSaturation = "UPDATE CookingTurn SET saturation = " + cookingTurn.isFull() + " WHERE id = " + cookingTurn.getId() + ";";
        PersistenceManager.executeUpdate(updateSaturation);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == CookingTurn.class) {
            CookingTurn cookingTurn = (CookingTurn) obj;
            return id == cookingTurn.getId();
        }
        return false;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public int getId() {
        return id;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public int getStartHour() {
        Calendar timeStart = Calendar.getInstance();
        timeStart.setTime(startTime);
        return timeStart.get(Calendar.HOUR_OF_DAY);
    }

    public int getStartMinute() {
        Calendar timeStart = Calendar.getInstance();
        timeStart.setTime(startTime);
        return timeStart.get(Calendar.MINUTE);
    }

    public String getTurnInfo() {
        return date + " (" + startTime.toString() + " - " + endTime.toString() + ")";
    }

    public String toString() {
        String turnInfo = getTurnInfo();
        String status;
        if (isFull())
            status = "Completo!";
        else
            status = "Disponibile!";
        return turnInfo + " status: " + status;
    }

}
