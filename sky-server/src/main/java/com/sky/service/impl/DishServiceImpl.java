package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

   @Override
   @Transactional
   public void InsertDish(DishDTO dishDTO){
       //我们下一步要操作dish这个表,使用dishDTO这个对象明显不符合情况,所以我们重新创建一个entity为dish
       Dish dish=new Dish();
       BeanUtils.copyProperties(dishDTO,dish);

       //向菜品表 中插入一条数据
       dishMapper.InsertDish(dish);

       //前面这一步向菜品表插入数据后会返回一个该菜品的id
       long dishId=dish.getId();

       //向口味表中插入一条数据
       List<DishFlavor> dishFlavors=dishDTO.getFlavors();
       if(dishFlavors!=null&&dishFlavors.size()>0){
           dishFlavors.forEach(dishFlavor -> {
               dishFlavor.setDishId(dishId);
           });
           dishFlavorMapper.insertFlavor(dishFlavors);
       }
   }

   @Override
    public PageResult selectDish(DishPageQueryDTO dishPageQueryDTO){
       PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
       Page<DishVO> page= dishMapper.pageQuery(dishPageQueryDTO);
       return new PageResult(page.getTotal(),page.getResult());
   }
}

