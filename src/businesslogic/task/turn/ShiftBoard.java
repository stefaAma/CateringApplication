package businesslogic.task.turn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ShiftBoard implements ShiftBoardInfo {
    private int id;
    private ObservableList<CookingTurn> cookingTurns;
    private String dateStart;
    private String dateEnd;

    private static ObservableList<ShiftBoard> shiftBoards = FXCollections.observableArrayList();

    private ShiftBoard(int id, ObservableList<CookingTurn> cookingTurns, String dateStart, String dateEnd) {
        this.id = id;
        this.cookingTurns = cookingTurns;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public static ObservableList<ShiftBoard> getShiftBoards() {
        if (shiftBoards.size() > 0)
            return shiftBoards;

        String dateNow = LocalDateTime.now().toString().substring(0, 10);

        System.out.println("ShiftBoard 17: dateNow = " + dateNow);

        String getShiftBoards = "SELECT * FROM ShiftBoard WHERE date_end >= " + dateNow + " ORDER BY date_start;";
        PersistenceManager.executeQuery(getShiftBoards, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String dateStart = rs.getString("date_start");
                String dateEnd = rs.getString("date_end");
                ObservableList<CookingTurn> cookingTurns = CookingTurn.getCookingTurnByShiftBoard(id);
                shiftBoards.add(new ShiftBoard(id, cookingTurns, dateStart, dateEnd));
            }
        });
        return shiftBoards;
    }

    public ObservableList<CookingTurn> getCookingTurns() {
        return cookingTurns;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public String toString() {
        return dateStart + " - " + dateEnd;
    }

}
