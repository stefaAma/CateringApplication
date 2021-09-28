package persistence;

import businesslogic.task.Cook;
import businesslogic.task.Task;
import businesslogic.task.TaskEventReceiver;
import businesslogic.task.TaskSummary;
import businesslogic.task.turn.CookingTurn;

import java.util.List;

public class TaskPersistence implements TaskEventReceiver {

    @Override
    public void updateTaskSummaryCreated(TaskSummary taskSummary) {
        TaskSummary.saveNewTaskSummary(taskSummary);
    }

    @Override
    public void updateTaskAdded(Task task , int taskSummaryID, int position) {
        Task.saveNewTask(task, taskSummaryID, position);
    }

    @Override
    public void updateTaskRemoved(Task task) {
        Task.removeTask(task.getId());
    }

    @Override
    public void updateTaskPositionChanged(int oldPosition, int newPosition, List<Task> tasks) {
        if (oldPosition < newPosition)
            for (int i = oldPosition ; i <= newPosition; i++)
                Task.updateTaskPosition(tasks.get(i).getId(), i);
        else
            for (int i = newPosition ; i <= oldPosition; i++)
                Task.updateTaskPosition(tasks.get(i).getId(), i);
    }

    @Override
    public void updateAssignedCook(Task task) {
        Task.updateCook(task);
    }

    @Override
    public void updateCookingTurnAssigned(Task task) {
        Task.updateCookingTurn(task);
    }

    @Override
    public void updateAssignmentsDeleted(Task task) {
        Task.deleteAssignments(task);
    }

    @Override
    public void updateDosesNeededAssigned(Task task) {
        Task.assignDosesNeeded(task);
    }

    @Override
    public void updateDosesPreparedAssigned(Task task) {
        Task.assignDosesPrepared(task);
    }

    @Override
    public void updateAssignedTimeRequested(Task task) {
        Task.assignTimeRequested(task);
    }

    @Override
    public void updateTaskSummaryRemoved(int id) {
        TaskSummary.removeTaskSummary(id);
    }

    @Override
    public void updateCookingTurnFull(CookingTurn cookingTurn) {
        CookingTurn.cookingTurnFull(cookingTurn);
    }

    @Override
    public void updateCookingTurnAvailable(CookingTurn cookingTurn) {
        CookingTurn.cookingTurnFull(cookingTurn);
    }

}
