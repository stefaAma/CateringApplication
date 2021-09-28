package businesslogic.task;

import businesslogic.UseCaseLogicException;
import businesslogic.task.turn.CookingTurn;
import businesslogic.task.turn.ShiftBoardInfo;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//TODO: evaluate the possibility of introduction of Cook availability for a specific CookingTurn

public class Cook implements ShiftBoardInfo {
    private User user;
    private Map<CookingTurn, Integer> turns;

    private static ObservableList<Cook> cooks = FXCollections.observableArrayList();
    private static Map<Integer, Cook> cookMap = new HashMap<>();

    private Cook() {
        turns = new HashMap<>();
    }

    /*public Cook(User user, Map<CookingTurn, Integer> turns) {
        this.user = user;
        this.turns = turns;
    }*/

    public static ObservableList<Cook> loadCooks() {
        if (cooks.size() > 0)
            return cooks;
        String getUsers = "SELECT * FROM Users, UserRoles WHERE id = user_id and role_id = 'c'";
        PersistenceManager.executeQuery(getUsers, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                cooks.add(loadCookById(rs.getInt("id")));
            }
        });
        return cooks;
    }

    public static Cook loadCookById(int cookId) {
        if (cookId == -1)
            return null;
        if (cookMap.containsKey(cookId))
            return cookMap.get(cookId);
        Cook cook = new Cook();
        cookMap.put(cookId, cook);
        cook.user = User.loadUserById(cookId);
        String getCooks = "SELECT * FROM Cook where user_id = " + cookId + ";";
        PersistenceManager.executeQuery(getCooks, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int cookingTurnId = rs.getInt("cooking_turn_id");
                CookingTurn cookingTurn = CookingTurn.loadCookingTurnById(cookingTurnId);
                if (cookingTurn == null)
                    throw new SQLException("Cook.loadCooks EXCEPTION: cookingTurn is null for user: " + cookId);
                cook.turns.put(cookingTurn, rs.getInt("remaining_time"));
            }
        });
        return cookMap.get(cookId);
    }

    public boolean assignTask(CookingTurn cookingTurn, int timeRequested) {
        if (turns.containsKey(cookingTurn)) {
            int remainingTime = turns.get(cookingTurn) - timeRequested;
            if (remainingTime >= 0) {
                turns.replace(cookingTurn, remainingTime);
                return true;
            }
        }
        else {
            int remainingTime = CookingTurn.getTurnLength(cookingTurn) - timeRequested;
            if (remainingTime >= 0) {
                turns.put(cookingTurn, remainingTime);
                return true;
            }
        }
        return false;
    }

    public void removeTask(CookingTurn cookingTurn, int timeRequested) throws Exception {
        if (!turns.containsKey(cookingTurn))
            throw new UseCaseLogicException();
        int timeLeft = turns.get(cookingTurn);
        int newTimeLeft = timeLeft + timeRequested;
        if (newTimeLeft == CookingTurn.getTurnLength(cookingTurn))
            turns.remove(cookingTurn);
        else
            turns.replace(cookingTurn, newTimeLeft);
    }

    public int getID() {
        return user.getId();
    }

    public int getRemainingTime(CookingTurn cookingTurn) {
        return turns.get(cookingTurn);
    }

    public String getUserName() {
        return user.getUserName();
    }

    public String toString() {
        return user.getUserName();
    }

}
