package com.aaa.repast.admin.project.system.dict.service;

import com.aaa.repast.admin.framework.utils.ShiroUtils;
import com.aaa.repast.admin.project.system.dict.domain.DictType;
import com.aaa.repast.admin.project.system.dict.mapper.DictDataMapper;
import com.aaa.repast.admin.project.system.dict.mapper.DictTypeMapper;
import com.aaa.repast.common.constant.UserConstants;
import com.aaa.repast.common.exception.BusinessException;
import com.aaa.repast.common.support.Convert;
import com.aaa.repast.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 业务层处理
 * 
 * @author Seven Lee
 */
@Service
public class DictTypeServiceImpl implements IDictTypeService
{
    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Autowired
    private DictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<DictType> selectDictTypeList(DictType dictType)
    {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    @Override
    public List<DictType> selectDictTypeAll()
    {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public DictType selectDictTypeById(Long dictId)
    {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 通过字典ID删除字典信息
     * 
     * @param dictId 字典ID
     * @return 结果
     */
    @Override
    public int deleteDictTypeById(Long dictId)
    {
        return dictTypeMapper.deleteDictTypeById(dictId);
    }

    /**
     * 批量删除字典类型
     * 
     * @param ids 需要删除的数据
     * @return 结果
     */
    @Override
    public int deleteDictTypeByIds(String ids) throws BusinessException
    {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds)
        {
            DictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0)
            {
                throw new BusinessException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
        }

        return dictTypeMapper.deleteDictTypeByIds(dictIds);
    }

    /**
     * 新增保存字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(DictType dictType)
    {
        dictType.setCreateBy(ShiroUtils.getLoginName());
        return dictTypeMapper.insertDictType(dictType);
    }

    /**
     * 修改保存字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int updateDictType(DictType dictType)
    {
        dictType.setUpdateBy(ShiroUtils.getLoginName());
        DictType oldDict = dictTypeMapper.selectDictTypeById(dictType.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        return dictTypeMapper.updateDictType(dictType);
    }

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(DictType dict)
    {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        DictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue())
        {
            return UserConstants.DICT_TYPE_NOT_UNIQUE;
        }
        return UserConstants.DICT_TYPE_UNIQUE;
    }
}
