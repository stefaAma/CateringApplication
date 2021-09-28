package businesslogic.task;

import businesslogic.UseCaseLogicException;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.CookingProcedure;
import businesslogic.task.turn.CookingTurn;
import businesslogic.task.turn.ShiftBoard;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO: implement/review query operations in every task's package class! (particularly Task and CookingTurn)
//TODO: check the query logic inside the WHERE clause ( use 'and or..' not ',' )

public class TaskManager {
    private TaskSummary taskSummary;
    private List<TaskEventReceiver> eventReceivers = new ArrayList<>();

    public TaskManager() {
        TaskSummary.setCurrentId();
        Task.setCurrentId();
    }

    public void addEventReceiver(TaskEventReceiver taskEventReceiver) {
        eventReceivers.add(taskEventReceiver);
    }

    public void removeEventReceiver(TaskEventReceiver taskEventReceiver) {eventReceivers.remove(taskEventReceiver);}

    public void createTaskSummary(ServiceInfo service) throws Exception {
        if (service == null)
            throw new UseCaseLogicException();
        taskSummary = TaskSummary.makeTaskSummary(service);
        notifyTaskSummaryCreated();
    }

    public void setTaskSummary(ServiceInfo service) throws Exception{
        if (service == null)
            throw new UseCaseLogicException();
        taskSummary = TaskSummary.loadTaskSummaryByService(service);
    }

    public void resetTaskSummary(ServiceInfo service) throws Exception {
        taskSummary = TaskSummary.loadTaskSummaryByService(service);
        taskSummary.removeAllTasks();
        TaskSummary.removeTaskSummaryReference(service.getId());
        notifyTaskSummaryRemoved();
        createTaskSummary(service);
    }

    public void addTask(CookingProcedure cookingProcedure) throws Exception {
        if (cookingProcedure == null || taskSummary == null)
            throw new UseCaseLogicException();
        taskSummary.addTask(cookingProcedure);
        notifyTaskAdded();
    }

    public void removeTask(Task task) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        int positionRemoved = taskSummary.getTasks().indexOf(task);
        taskSummary.removeTask(task);
        notifyTaskRemoved(task);
        int tasksLastElem = taskSummary.getTasks().size() - 1;
        if (positionRemoved <= tasksLastElem)
            notifyTaskPositionChanged(positionRemoved, tasksLastElem);
    }

    public void orderList(Task task, int newPosition) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        int oldPosition = taskSummary.getTasks().indexOf(task);
        if (oldPosition != newPosition) {
            taskSummary.orderList(task, newPosition);
            notifyTaskPositionChanged(oldPosition, newPosition);
        }
    }

    public boolean assignCook(Task task, Cook cook) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        boolean assigned = taskSummary.assignCook(task, cook);
        if (assigned)
            notifyAssignedCook(task);
        return assigned;
    }

    public boolean assignCookingTurn(Task task, CookingTurn cookingTurn) throws Exception {
        if (!checkCookingTurnDateTime(cookingTurn) || cookingTurn.isFull())
            return false;
        if (taskSummary == null)
            throw new UseCaseLogicException();
        boolean assigned = taskSummary.assignCookingTurn(task, cookingTurn);
        if (assigned)
            notifyAssignedCookingTurn(task);
        return assigned;
    }

    public void deleteAssignments(Task task) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        Cook cook = task.getCook();
        CookingTurn cookingTurn = task.getCookingTurn();
        if (cook != null || cookingTurn != null) {
            taskSummary.deleteAssignments(task);
            notifyAssignmentsDeleted(task);
        }
    }

    public void assignDosesNeeded(Task task, String dosesNeeded) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        taskSummary.assignDosesNeeded(task, dosesNeeded);
        notifyDosesNeededAssigned(task);
    }

    public void assignDosesPrepared(Task task, String dosesPrepared) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        taskSummary.assignDosesPrepared(task, dosesPrepared);
        notifyDosesPreparedAssigned(task);
    }

    public boolean assignTimeRequested(Task task, int timeRequested) throws Exception {
        if (taskSummary == null)
            throw new UseCaseLogicException();
        boolean assigned = taskSummary.assignTimeRequested(task, timeRequested);
        if (assigned)
            notifyAssignedTimeRequested(task);
        return assigned;
    }

    public ObservableList<ShiftBoard> getShiftBoards() {
        return ShiftBoard.getShiftBoards();
    }

    public void setCookingTurnFull(CookingTurn cookingTurn) {
        cookingTurn.setFull(true);
        notifyCookingTurnFull(cookingTurn);
    }

    public void setCookingTurnAvailable(CookingTurn cookingTurn) {
        cookingTurn.setFull(false);
        notifyCookingTurnAvailable(cookingTurn);
    }

    public TaskSummary getTaskSummary() {
        return taskSummary;
    }

    public int getTaskListSize() throws Exception{
        if (taskSummary == null)
            throw new UseCaseLogicException();
        return taskSummary.getTaskListSize();
    }

    public boolean checkCookingTurnDateTime(CookingTurn cookingTurn) {
        LocalDateTime now = LocalDateTime.now();
        String dateNow = now.toString().substring(0, 9);
        int checkDate = dateNow.compareTo(cookingTurn.getDate());
        if (checkDate < 0)
            return true;
        if (checkDate > 0)
            return false;
        int nowHour = now.getHour();
        int nowMinute = now.getMinute();
        if (cookingTurn.getStartHour() > nowHour || (cookingTurn.getStartHour() == nowHour && cookingTurn.getStartMinute() > nowMinute))
            return true;
        return false;
    }

    private void notifyTaskSummaryCreated() {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateTaskSummaryCreated(taskSummary);
    }

    private void notifyTaskAdded() {
        List<Task> tasks = taskSummary.getTasks();
        Task taskAdded = tasks.get(tasks.size() - 1);
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateTaskAdded(taskAdded, taskSummary.getId(), tasks.size() - 1);
    }

    private void notifyTaskRemoved(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateTaskRemoved(task);
    }

    private void notifyTaskPositionChanged(int oldPosition, int newPosition) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateTaskPositionChanged(oldPosition, newPosition, taskSummary.getTasks());
    }

    private void notifyAssignedCook(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateAssignedCook(task);
    }

    private void notifyAssignedCookingTurn(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateCookingTurnAssigned(task);
    }

    private void notifyAssignmentsDeleted(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateAssignmentsDeleted(task);
    }

    private void notifyDosesNeededAssigned(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateDosesNeededAssigned(task);
    }

    private void notifyDosesPreparedAssigned(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateDosesPreparedAssigned(task);
    }

    private void notifyAssignedTimeRequested(Task task) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateAssignedTimeRequested(task);
    }

    private void notifyTaskSummaryRemoved() {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateTaskSummaryRemoved(taskSummary.getId());
    }

    private void notifyCookingTurnFull(CookingTurn cookingTurn) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateCookingTurnFull(cookingTurn);
    }

    private void notifyCookingTurnAvailable(CookingTurn cookingTurn) {
        for (TaskEventReceiver eventReceiver : eventReceivers)
            eventReceiver.updateCookingTurnAvailable(cookingTurn);
    }

}
