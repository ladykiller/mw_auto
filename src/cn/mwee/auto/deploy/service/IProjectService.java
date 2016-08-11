package cn.mwee.auto.deploy.service;

import cn.mwee.auto.auth.model.AuthUser;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
public interface IProjectService {
    /**
     * 获取项目所有模板中的区域服务状态
     * @return
     */
    List<Map<String,Object>> getProjectZonesStatus(Integer projectId);

    List<AuthUser> getUsers4Project(Integer projectId);
}
