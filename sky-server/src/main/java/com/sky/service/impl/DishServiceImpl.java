package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void InsertDish(DishDTO dishDTO) {
        //我们下一步要操作dish这个表,使用dishDTO这个对象明显不符合情况,所以我们重新创建一个entity为dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表 中插入一条数据
        dishMapper.InsertDish(dish);

        //前面这一步向菜品表插入数据后会返回一个该菜品的id
        long dishId = dish.getId();

        //向口味表中插入一条数据
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertFlavor(dishFlavors);
        }
    }

    @Override
    public PageResult selectDish(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        for (Long id : ids) {
            //判断这个id中包括的菜品中有没有状态为启售中的,有的话,直接抛异常
            Dish dish = new Dish();
            dish = dishMapper.select(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

            }

            //判断这个菜品是否包含在套餐中,如果在套餐中,直接抛异常
            List<SetmealDish> setmealDishes = setmealDishMapper.selectSetmeal(id);
            if (setmealDishes != null && setmealDishes.size() > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

            }

            //删除菜品
//           dishMapper.deleteDish(id);

            //断菜品有没有关联口味,如果有,删除掉
//           dishFlavorMapper.deleteFlavor(id);
        }

        //批量删除菜品
        dishMapper.deleteDish(ids);
        //批量删除口味
        dishFlavorMapper.deleteFlavor(ids);
    }

    @Override
    @Transactional
    public DishVO getById(Long id) {
        //查询dish表
        Dish dish = dishMapper.select(id);

        //查询dish_flavor表
        List<DishFlavor> dishFlavor = dishFlavorMapper.getById(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavor);

        return dishVO;
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        //操作dish表,修改一条菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //操作dish_flavor表
        //先删除原有的口味数据
        dishFlavorMapper.deleteFlavorById(dishDTO.getId());

        //再添加口味数据
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertFlavor(dishFlavors);
        }

    }
}

