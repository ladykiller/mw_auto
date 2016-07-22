package cn.mwee.auto.deploy.service;

import cn.mwee.auto.deploy.contract.QueryTasksRequest;
import cn.mwee.auto.deploy.contract.QueryTasksResult;
import cn.mwee.auto.deploy.model.AutoTask;

import java.util.List;

/**
 * Created by huming on 16/6/24.
 */
public interface ITaskManagerService {

    boolean addTask(AutoTask task);

    AutoTask getTask(Integer taskId);

    boolean deleteTask(Integer taskId);

    boolean modifyTask(AutoTask task);

    QueryTasksResult getAutoTasks(QueryTasksRequest request);
}
