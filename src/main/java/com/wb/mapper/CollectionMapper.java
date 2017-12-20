package com.wb.mapper;


import com.wb.model.Collection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectionMapper {

    List<Collection> listCreatingCollectionByUserId(Integer userId);

    Integer selectUserIdByCollectionId(@Param("collectionId") Integer collectionId);

    Collection selectCollectionByCollectionId(Integer collectionId);

    void insertCollection(Collection collection);

    List<Collection> listCollectionByCollectionId(List<Integer> idList);
}
