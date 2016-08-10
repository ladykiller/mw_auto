package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.service.IProjectService;
import cn.mwee.auto.deploy.service.ITemplateManagerService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
@Service
public class ProjectService implements IProjectService {

    @Resource
    private ITemplateManagerService templateManagerService;

    @Override
    public List<Map<String, Object>> getProjectZonesStatus(Integer projectId) {
        List<AutoTemplate> templates = templateManagerService.getTemplates4Project(projectId);
        if (CollectionUtils.isEmpty(templates)) return new ArrayList<>();
        List<Map<String, Object>> resultList = new ArrayList<>(templates.size());
        templates.forEach(template -> {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("template",template);
            map.put("zoneStates",templateManagerService.getTemplateZoneStatus(template.getId()));
            resultList.add(map);
        });
        return resultList;
    }
}
