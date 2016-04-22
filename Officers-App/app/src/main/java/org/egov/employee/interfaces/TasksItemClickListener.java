package org.egov.employee.interfaces;

import org.egov.employee.data.Task;

/**
 * Created by egov on 15/12/15.
 */
public interface TasksItemClickListener {

    public void onTaskItemClicked(Task taskToActivity);

    public interface TaskItemClickedIndex{
        public void onTaskItemClickedIdx(Task taskFromAdapter);
    }

}
