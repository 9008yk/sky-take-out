package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insertFlavor(List<DishFlavor> dishFlavors);

    void deleteFlavor(List<Long> ids);
}
