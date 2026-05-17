package com.dismai.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dismai.entity.UserMobile;
import com.dismai.enums.BaseCode;
import com.dismai.exception.DismaiFrameException;
import com.dismai.mapper.UserMobileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserMobileService extends ServiceImpl<UserMobileMapper, UserMobile> {

	@Autowired
	private UidGenerator uidGenerator;

	public UserMobile getByMobile(String mobile) {
		LambdaQueryWrapper<UserMobile> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(UserMobile::getMobile, mobile);
		return getOne(queryWrapper, false);
	}

	public UserMobile getByUserId(Long userId) {
		LambdaQueryWrapper<UserMobile> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(UserMobile::getUserId, userId);
		return getOne(queryWrapper, false);
	}

	public boolean existsByMobile(String mobile) {
		LambdaQueryWrapper<UserMobile> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(UserMobile::getMobile, mobile);
		return count(queryWrapper) > 0;
	}

	@Transactional(rollbackFor = Exception.class)
	public void bindMobile(Long userId, String mobile) {
		UserMobile existMobile = getByMobile(mobile);
		if (Objects.nonNull(existMobile) && !Objects.equals(existMobile.getUserId(), userId)) {
			throw new DismaiFrameException(BaseCode.USER_EXIST);
		}

		UserMobile userMobile = getByUserId(userId);
		if (Objects.isNull(userMobile)) {
			userMobile = new UserMobile();
			userMobile.setId(uidGenerator.getUid());
			userMobile.setUserId(userId);
			userMobile.setMobile(mobile);
			save(userMobile);
			return;
		}

		userMobile.setMobile(mobile);
		updateById(userMobile);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateMobile(Long userId, String mobile) {
		bindMobile(userId, mobile);
	}
}
