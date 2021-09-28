package businesslogic.task;

import businesslogic.recipe.CookingProcedure;
import businesslogic.task.turn.CookingTurn;
import businesslogic.task.turn.ShiftBoardInfo;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Task implements ShiftBoardInfo {
    private CookingProcedure cookingProcedure;
    private int id;
    private Cook cook;
    private CookingTurn cookingTurn;
    private int timeRequested;
    private String dosesNeeded;
    private String dosesPrepared;
    //private boolean completed;

    private static int current_id = 0;
    private static Map<Integer, Task> taskMap = new HashMap<>();

    private Task() {}

    public Task (CookingProcedure cookingProcedure) {
        current_id++;
        id = current_id;
        this.cookingProcedure = cookingProcedure;
        this.timeRequested = 0;
        this.cook = null;
        this.cookingTurn = null;
        this.dosesNeeded = "N/A";
        this.dosesPrepared = "N/A";
    }

    /*private Task (int id, CookingProcedure cookingProcedure, int timeRequested, Cook cook, CookingTurn cookingTurn,
                  String dosesNeeded, String dosesPrepared) {
        this.id = id;
        this.cookingProcedure = cookingProcedure;
        this.timeRequested = timeRequested;
        this.cook = cook;
        this.cookingTurn = cookingTurn;
        this.dosesNeeded = dosesNeeded;
        this.dosesPrepared = dosesPrepared;
    }*/

    public static void setCurrentId() {
        String getMaxID = "SELECT MAX(id) FROM Task";
        PersistenceManager.executeQuery(getMaxID, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                current_id = rs.getInt("MAX(id)");
            }
        });
    }

    public static Task loadTaskById(int taskId) {
        if (taskMap.containsKey(taskId))
            return taskMap.get(taskId);
        String getTask = "SELECT * FROM Task WHERE id = " + taskId + ";";
        PersistenceManager.executeQuery(getTask, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                CookingProcedure cookingProcedure = CookingProcedure.loadProcedureById(rs.getInt("cooking_procedure_id"));
                Task task = new Task();
                task.id = taskId;
                task.cookingProcedure = cookingProcedure;
                taskMap.put(taskId, task);
                task.timeRequested = rs.getInt("time_requested");
                task.dosesNeeded = rs.getString("doses_needed");
                task.dosesPrepared = rs.getString("doses_prepared");
                task.cookingTurn = CookingTurn.loadCookingTurnById(rs.getInt("cooking_turn_id"));
                task.cook = Cook.loadCookById(rs.getInt("user_id"));
            }
        });
        return taskMap.get(taskId);
    }

    public static void saveNewTask(Task task, int taskSummaryID, int position) {
        String taskInsert = "INSERT INTO catering.Task (id, cooking_procedure_id, user_id, cooking_turn_id, time_requested, doses_needed, doses_prepared, task_summary_id" +
                ", position) VALUES (" + task.getId() + ", " + task.getCookingProcedure().getProcedure_id() + ", " + -1 + ", " + -1 + ", " + task.getTimeRequested() + ", '" +
                task.getDosesNeeded() + "', '" + task.getDosesPrepared() + "', " + taskSummaryID + ", " + position + ");";
        PersistenceManager.executeUpdate(taskInsert);
    }

    public static void removeTask(int id) {
        updateCookAssociation(id);
        String deleteTask = "DELETE FROM Task WHERE id = " + id + ";";
        PersistenceManager.executeUpdate(deleteTask);
    }

    public static void updateTaskPosition(int id, int position) {
        String updatePosition = "UPDATE Task Set position = " + position + " WHERE id = " + id + ";";
        PersistenceManager.executeUpdate(updatePosition);
    }

    public static void removeTaskReference(int id) {
        taskMap.remove(id);
    }

    private static void updateCookAssociation(int task_id) {
        String getTask = "SELECT * FROM Task WHERE id = " + task_id + ";";
        PersistenceManager.executeQuery(getTask, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int user_id = rs.getInt("user_id");
                int cooking_turn_id = rs.getInt("cooking_turn_id");
                int timeRequested = rs.getInt("time_requested");
                if (user_id != -1 && cooking_turn_id != - 1) {
                    String getCook = "SELECT * FROM Cook WHERE user_id = " + user_id + " and cooking_turn_id = " + cooking_turn_id + ";";
                    PersistenceManager.executeQuery(getCook, new ResultHandler() {
                        @Override
                        public void handle(ResultSet rs) throws SQLException {
                            int remaining_time = rs.getInt("remaining_time");
                            int newRemainingTime = remaining_time + timeRequested;
                            CookingTurn cookingTurn = CookingTurn.loadCookingTurnById(cooking_turn_id);
                            int turn_time = CookingTurn.getTurnLength(cookingTurn);
                            if (newRemainingTime == turn_time) {
                                String delete = "DELETE FROM Cook WHERE user_id = " + user_id + " and cooking_turn_id = " + cooking_turn_id + ";";
                                PersistenceManager.executeUpdate(delete);
                            }
                            else  {
                                String update = "UPDATE Cook Set remaining_time = " + newRemainingTime + " WHERE user_id = " + user_id + " and cooking_turn_id = " +
                                        cooking_turn_id + ";";
                                PersistenceManager.executeUpdate(update);
                            }
                        }
                    });
                }
            }
        });
    }

    public static void updateCook(Task task) {
        updateAssociation(task, "c");
    }

    public static void updateCookingTurn(Task task) {
       updateAssociation(task, "t");
    }

    private static void updateAssociation(Task task, String type) {
        updateCookAssociation(task.getId());
        Cook cook = task.getCook();
        CookingTurn cookingTurn = task.getCookingTurn();
        String update;
        Object object;
        if (type.charAt(0) == 'c') {
            update = "UPDATE Task Set user_id = " + cook.getID() + " WHERE id = " + task.getId() + ";";
            object = cookingTurn;
        }
        else {
            update = "UPDATE Task Set cooking_turn_id = " + cookingTurn.getId() + " WHERE id = " + task.getId() + ";";
            object = cook;
        }
        PersistenceManager.executeUpdate(update);
        if (object != null) {
            String getCook = "SELECT * FROM Cook WHERE user_id = " + cook.getID() + " and cooking_turn_id = " + cookingTurn.getId() + ";";
            List<Boolean> exists = new ArrayList<>();
            exists.add(false);
            int remainingTime = cook.getRemainingTime(cookingTurn);
            PersistenceManager.executeQuery(getCook, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    exists.remove(0);
                    exists.add(true);
                    String updateRemainingTime = "UPDATE Cook SET remaining_time = " + remainingTime + " WHERE user_id = " + cook.getID() + " and cooking_turn_id = "
                            + cookingTurn.getId() + ";";
                    PersistenceManager.executeUpdate(updateRemainingTime);
                }
            });
            if (!exists.get(0)) {
                String insertRemainingTime = "INSERT INTO catering.Cook (user_id, cooking_turn_id, remaining_time) VALUES (" + cook.getID() + ", " + cookingTurn.getId()
                        + ", " + remainingTime + ");";
                PersistenceManager.executeUpdate(insertRemainingTime);
            }
        }
    }

    public static void deleteAssignments(Task task) {
        updateCookAssociation(task.getId());
        String deleteAssignments = "UPDATE Task Set user_id = " + -1 + ", cooking_turn_id = " + - 1 + " WHERE id = " + task.getId() + ";";
        PersistenceManager.executeUpdate(deleteAssignments);
    }

    public static void assignDosesNeeded(Task task) {
        String assignDosesNeeded = "UPDATE Task Set doses_needed = '" + task.getDosesNeeded() +  "' WHERE id = " + task.getId() + ";";
        PersistenceManager.executeUpdate(assignDosesNeeded);
    }

    public static void assignDosesPrepared(Task task) {
        String assignDosesPrepared = "UPDATE Task Set doses_prepared = '" + task.getDosesPrepared() +  "' WHERE id = " + task.getId() + ";";
        PersistenceManager.executeUpdate(assignDosesPrepared);
    }

    public static void assignTimeRequested(Task task) {
        String assignTime = "UPDATE Task Set time_requested = " + task.getTimeRequested() +  " WHERE id = " + task.getId() + ";";
        PersistenceManager.executeUpdate(assignTime);
        Cook cook = task.getCook();
        CookingTurn cookingTurn = task.getCookingTurn();
        if (cook != null && cookingTurn != null) {
            int newRemainingTime = cook.getRemainingTime(cookingTurn);
            String updateRemainingTime = "UPDATE Cook Set remaining_time = " + newRemainingTime + " WHERE user_id = " + cook.getID() + " and cooking_turn_id = "
                    + cookingTurn.getId() + ";";
            PersistenceManager.executeUpdate(updateRemainingTime);
        }
    }

    public boolean assignCook(Cook cook) throws Exception {
        if (cookingTurn != null && timeRequested <= 0)
            return false;
        if (cookingTurn == null) {
            this.cook = cook;
            return true;
        }
        if (!cook.assignTask(this.cookingTurn, this.timeRequested))
            return false;
        if (this.cook != null)
            this.cook.removeTask(this.cookingTurn, this.timeRequested);
        this.cook = cook;
        return true;
    }

    public boolean assignCookingTurn(CookingTurn cookingTurn) throws Exception {
        if (cook != null && timeRequested <= 0)
            return false;
        if (cook == null) {
            if (this.cookingTurn != null)
                this.cookingTurn.removeTask(this);
            this.cookingTurn = cookingTurn;
            this.cookingTurn.addTask(this);
            return true;
        }
        if (!cook.assignTask(cookingTurn, timeRequested))
            return false;
        if (this.cookingTurn != null) {
            cook.removeTask(this.cookingTurn, timeRequested);
            this.cookingTurn.removeTask(this);
        }
        this.cookingTurn = cookingTurn;
        this.cookingTurn.addTask(this);
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj.getClass() == Task.class)) {
            Task task = (Task) obj;
            if (id == task.getId())
                return true;
        }
        return false;
    }

    public void deleteAssignments() throws Exception {
        if (cookingTurn != null) {
            cookingTurn.removeTask(this);
            if (cook != null)
                cook.removeTask(cookingTurn, timeRequested);
        }
        cook = null;
        cookingTurn = null;
    }

    public boolean setTimeRequested(int newTimeRequested) {
        if (cook != null && cookingTurn != null) {
            if (!cook.assignTask(cookingTurn, newTimeRequested - timeRequested))
                return false;
        }
        timeRequested = newTimeRequested;
        return true;
    }

    public void setDosesNeeded(String dosesNeeded) {
        this.dosesNeeded = dosesNeeded;
    }

    public void setDosesPrepared(String dosesPrepared) {
        this.dosesPrepared = dosesPrepared;
    }

    public int getId() {
        return id;
    }

    public Cook getCook() {
        return cook;
    }

    public CookingProcedure getCookingProcedure() {
        return cookingProcedure;
    }

    public CookingTurn getCookingTurn() {
        return cookingTurn;
    }

    public int getTimeRequested() {
        return timeRequested;
    }

    public String getDosesNeeded() {
        return dosesNeeded;
    }

    public String getDosesPrepared() {
        return dosesPrepared;
    }

    public String toString() {
        String cookName;
        String turnInfo;
        if (cook != null)
            cookName = cook.getUserName();
        else
            cookName = "Nessun Cuoco";
        if (cookingTurn != null)
            turnInfo = cookingTurn.getTurnInfo();
        else
            turnInfo = "Nessun Turno";
        return cookingProcedure.getName() + " | " + cookName + " | " + turnInfo + " | Tempo Richiesto: " + timeRequested + " | Dosi già Preparate: " +
                dosesPrepared + " | Dosi da Preparare: " + dosesNeeded;
    }

    /*public boolean setCompleted() {
        if (cook != null && cookingTurn != null)
            return completed = true;
        return false;
    }

    public boolean isCompleted() {
        return completed;
    }*/

}
