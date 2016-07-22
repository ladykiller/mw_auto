package cn.mwee.auto.deploy.service;

import cn.mwee.auto.deploy.contract.QueryTasksRequest;
import cn.mwee.auto.deploy.contract.QueryTasksResult;
import cn.mwee.auto.deploy.model.AutoTask;

import java.util.List;
import java.util.Set;

/**
 * Created by huming on 16/6/24.
 */
public interface ITaskManagerService {

    boolean addTask(AutoTask task);

    AutoTask getTask(Integer taskId);

    boolean deleteTask(Integer taskId);

    QueryTasksResult getAutoTasks(QueryTasksRequest request);

    List<AutoTask> getAutoTasksByIds (Set<Integer> ids);
}
