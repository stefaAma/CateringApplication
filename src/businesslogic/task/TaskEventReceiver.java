package businesslogic.task;

import businesslogic.task.turn.CookingTurn;

import java.util.List;

public interface TaskEventReceiver {

    void updateTaskSummaryCreated(TaskSummary taskSummary);

    void updateTaskAdded(Task task , int taskSummaryID, int position);

    void updateTaskRemoved(Task task);

    void updateTaskPositionChanged(int oldPosition, int newPosition, List<Task> tasks);

    void updateAssignedCook(Task task);

    void updateCookingTurnAssigned(Task task);

    void updateAssignmentsDeleted(Task task);

    void updateDosesNeededAssigned(Task task);

    void updateDosesPreparedAssigned(Task task);

    void updateAssignedTimeRequested(Task task);

    void updateTaskSummaryRemoved(int id);

    void updateCookingTurnFull(CookingTurn cookingTurn);

    void updateCookingTurnAvailable(CookingTurn cookingTurn);

}
