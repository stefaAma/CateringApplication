package businesslogic.task;
import businesslogic.UseCaseLogicException;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import businesslogic.recipe.CookingProcedure;
import businesslogic.task.turn.CookingTurn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TaskSummary {
    private int id;
    private ServiceInfo service;
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    private static int current_id = 0;
    private static Map<Integer, TaskSummary> taskSummaryMap = new HashMap<>();

    private TaskSummary(ServiceInfo service) throws Exception {
        current_id++;
        id = current_id;
        this.service = service;
        Menu menu = this.service.getMenu();
        if (menu == null)
            throw new UseCaseLogicException();
        List<CookingProcedure> cookingProcedures = menu.getCookingProcedures();
        for (CookingProcedure cookingProcedure : cookingProcedures) {
            Task task = new Task(cookingProcedure);
            tasks.add(task);
        }
    }

    private TaskSummary(int id, ServiceInfo service, ObservableList<Task> tasks) {
        this.id = id;
        this.service = service;
        this.tasks = tasks;
    }

    public static TaskSummary makeTaskSummary(ServiceInfo serviceInfo) throws Exception {
        return new TaskSummary(serviceInfo);
    }

    public static void setCurrentId() {
        String getMaxID = "SELECT MAX(id) FROM TaskSummary;";
        PersistenceManager.executeQuery(getMaxID, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                current_id = rs.getInt("MAX(id)");
            }
        });
    }

    public static TaskSummary loadTaskSummaryByService(ServiceInfo service) {
        if (taskSummaryMap.containsKey(service.getId()))
            return taskSummaryMap.get(service.getId());
        String getTaskSummary = "SELECT * FROM TaskSummary WHERE service_id = " + service.getId() + ";";
        PersistenceManager.executeQuery(getTaskSummary, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String getTasks = "SELECT * FROM Task WHERE task_summary_id = " + id + " ORDER BY position;";
                ObservableList<Task> tasks = FXCollections.observableArrayList();
                PersistenceManager.executeQuery(getTasks, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        int id = rs.getInt("id");
                        tasks.add(Task.loadTaskById(id));
                    }
                });
                taskSummaryMap.put(service.getId(), new TaskSummary(id, service, tasks));
            }
        });
        return taskSummaryMap.get(service.getId());
    }

    public static void saveNewTaskSummary(TaskSummary taskSummary) {
        String taskSummaryInsert = "INSERT INTO catering.TaskSummary (id, service_id) VALUES (" + taskSummary.getId() + ", " + taskSummary.getService().getId() + ");";
        PersistenceManager.executeUpdate(taskSummaryInsert);
        for (Task task : taskSummary.getTasks())
            Task.saveNewTask(task, taskSummary.getId(), taskSummary.getTasks().indexOf(task));
    }

    public static void removeTaskSummaryReference(int id) {
        taskSummaryMap.remove(id);
    }

    public static void removeTaskSummary(int id) {
        String getTasks = "SELECT * FROM Task WHERE task_summary_id = " + id + ";";
        PersistenceManager.executeQuery(getTasks, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task.removeTask(rs.getInt("id"));
            }
        });
        String taskSummaryDelete = "DELETE FROM TaskSummary WHERE id = " + id + ";";
        PersistenceManager.executeUpdate(taskSummaryDelete);
    }

    public void addTask(CookingProcedure cookingProcedure) {
        tasks.add(new Task(cookingProcedure));
    }

    public void orderList(Task task, int position) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        tasks.remove(task);
        tasks.add(position, task);
    }

    public void removeTask(Task task) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        task.deleteAssignments();
        Task.removeTaskReference(task.getId());
        tasks.remove(task);
    }

    public void removeAllTasks() {
        for (Task task : tasks) {
            try {
                task.deleteAssignments();
                Task.removeTaskReference(task.getId());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        tasks.clear();
    }

    public boolean assignCook(Task task, Cook cook) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        if (checkCookAssignment(task, cook))
            return task.assignCook(cook);
        return false;
    }

    public boolean assignCookingTurn(Task task, CookingTurn cookingTurn) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        if (checkCookingTurnAssignment(task, cookingTurn))
            return task.assignCookingTurn(cookingTurn);
        return false;
    }

    public void deleteAssignments(Task task) throws Exception {
       if (!tasks.contains(task))
           throw new UseCaseLogicException();
        task.deleteAssignments();
    }

    public boolean assignTimeRequested(Task task, int newTimeRequested) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        return task.setTimeRequested(newTimeRequested);
    }

    public void assignDosesNeeded(Task task, String dosesNeeded) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        task.setDosesNeeded(dosesNeeded);
    }

    public void assignDosesPrepared(Task task, String dosesPrepared) throws Exception {
        if (!tasks.contains(task))
            throw new UseCaseLogicException();
        task.setDosesPrepared(dosesPrepared);
    }

    private boolean checkCookAssignment(Task task, Cook assignment) {
        for (Task otherTask : tasks) {
            if (task != otherTask) {
                if (otherTask.getCookingProcedure() == task.getCookingProcedure() && otherTask.getCook() == assignment && otherTask.getCookingTurn() == task.getCookingTurn())
                    return false;
            }
        }
        return true;
    }

    private boolean checkCookingTurnAssignment(Task task, CookingTurn assignment) {
        for (Task otherTask : tasks) {
            if (task != otherTask) {
                if (task.getCookingProcedure() == otherTask.getCookingProcedure() && otherTask.getCookingTurn() == assignment && otherTask.getCook() == task.getCook())
                    return false;
            }
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public ServiceInfo getService() {
        return service;
    }

    public int getTaskListSize() {
        return tasks.size();
    }

}
