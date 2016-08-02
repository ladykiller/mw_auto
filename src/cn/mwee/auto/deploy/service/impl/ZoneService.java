package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
import cn.mwee.auto.deploy.contract.task.QueryTasksResult;
import cn.mwee.auto.deploy.contract.zone.QueryZoneRequest;
import cn.mwee.auto.deploy.contract.zone.QueryZonesResult;
import cn.mwee.auto.deploy.dao.ZoneMapper;
import cn.mwee.auto.deploy.model.AutoTask;
import cn.mwee.auto.deploy.model.AutoTaskExample;
import cn.mwee.auto.deploy.model.Zone;
import cn.mwee.auto.deploy.model.ZoneExample;
import cn.mwee.auto.deploy.service.IZoneService;
import cn.mwee.auto.deploy.util.AutoConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by huming on 16/8/2.
 */
@Service
public class ZoneService implements IZoneService{

    @Autowired
    private ZoneMapper zoneMapper;

    @Override
    public QueryZonesResult queryZones(QueryZoneRequest req)
    {
        ZoneExample e = new ZoneExample();
        ZoneExample.Criteria c = e.createCriteria();

        e.setOrderByClause("id DESC");

        QueryZonesResult rs = new QueryZonesResult();
        BaseQueryResult<Zone> qrs = BaseModel.selectByPage(zoneMapper, e, req.getPage());

        rs.setList(qrs.getList());
        rs.setPage(qrs.getPage());

        return rs;
    }

    @Override
    public int addZone(Zone zone)
    {
        zone.setCreateTime(new Date());
        return zoneMapper.insertSelective(zone) > 0 ? zone.getId() : 0;
    }

    @Override
    public Zone getZone(int zoneId)
    {
        return zoneMapper.selectByPrimaryKey(zoneId);
    }

    @Override
    public boolean deleteZone(int zoneId) {
        return zoneMapper.deleteByPrimaryKey(zoneId) > 0;
    }

    @Override
    public boolean modifyZone(Zone zone) {
        zone.setUpdateTime(new Date());
        return zoneMapper.updateByPrimaryKeySelective(zone) > 0;
    }
}
