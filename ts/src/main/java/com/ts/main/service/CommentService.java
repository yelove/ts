package com.ts.main.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ts.main.bean.model.Comment;
import com.ts.main.bean.model.User;
import com.ts.main.bean.vo.CommentDto;
import com.ts.main.bean.vo.CommentVo;
import com.ts.main.common.CommentStatus;
import com.ts.main.mapper.CommentMapper;
import com.ts.main.utils.TimeUtils4book;

@Service
public class CommentService {

	@Autowired
	private CommentMapper commentMapper;

	@Autowired
	private UserService userService;

	@Autowired
	RedisService<Long> commentIdsService;

	@Autowired
	RedisService<Comment> commentService;

	/**
	 * 个人日记前缀
	 */
	public static final String BookComment_List = "BkCmt_List_";

	private static Cache<String, Comment> comment4096 = CacheBuilder.newBuilder().softValues()
			.expireAfterAccess(30, TimeUnit.MINUTES).initialCapacity(512).maximumSize(32768).build();

	public CommentVo getCommentByBookId(Long bookid) {
		List<Long> lis = commentIdsService.getList(BookComment_List + bookid, Long.class);
		List<Comment> rl = null;
		boolean flag = true;
		if (null == lis || lis.size() == 0) {
			rl = commentMapper.getCommentByBookId(bookid);
			if (null != rl && rl.size() > 0) {
				flag = false;
			}
		} else {
			flag = false;
		}
		if (flag) {
			return null;
		}
		Map<Long, Comment> rm = Maps.newHashMap();
		List<CommentDto> hot = Lists.newArrayList();
		List<CommentDto> normal = Lists.newArrayList();
		if (null != rl && rl.size() > 0) {
			for (Comment cmt : rl) {
				long id = cmt.getId();
				if (cmt.getIsdel().intValue() == CommentStatus.NOMAL.getValue()) {
					normal.add(covent(cmt));
				} else {
					hot.add(covent(cmt));
				}
				rm.put(id, cmt);
				comment4096.put(String.valueOf(id), cmt);
				commentService.hSet(RedisService.COMMENT_KEY, String.valueOf(id), cmt);
			}
			commentIdsService.addAll2ListLeft(BookComment_List + bookid, lis);
		} else {
			rl = Lists.newArrayList();
			for (Long id : lis) {
				Comment cmt = getByid(id);
				if (null != cmt) {
					if (cmt.getIsdel().intValue() == CommentStatus.NOMAL.getValue()) {
						normal.add(covent(cmt));
					} else {
						hot.add(covent(cmt));
					}
					rm.put(id, cmt);
				}
			}
		}
		CommentVo cmtv = new CommentVo();
		int total = 0;
		if (!hot.isEmpty()) {
			cmtv.setHotList(hot);
			total += hot.size();
		}
		if (!normal.isEmpty()) {
			cmtv.setNomalList(normal);
			total += normal.size();
		}
		cmtv.setTotalSize(total);
		return cmtv;
	}

	private CommentDto covent(Comment cmt) {
		CommentDto dto = new CommentDto();
		dto.setBookid(cmt.getBookid());
		dto.setComment(cmt.getComment());
		dto.setCreatetime(cmt.getCreatetime());
		dto.setCreateTimeStr(TimeUtils4book.long2Str(cmt.getCreatetime()));
		dto.setIsdel(cmt.getIsdel());
		dto.setUserid(cmt.getUserid());
		User user = userService.getUserBiIdWithCache(cmt.getUserid());
		dto.setUserimg(user.getName());
		dto.setUsername(user.getRealname());
		return dto;
	}

	public Comment getByid(final Long id) {
		final String key = String.valueOf(id);
		try {
			return comment4096.get(key, new Callable<Comment>() {
				@Override
				public Comment call() throws Exception {
					Comment cmt = commentService.hGet(RedisService.COMMENT_KEY, key);
					if (null == cmt) {
						cmt = commentMapper.selectByPrimaryKey(id);
						if (null != cmt) {
							commentService.hSet(RedisService.COMMENT_KEY, key, cmt);
						}
					}
					return cmt;
				}
			});
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int saveComment(Comment cmt) {
		long ct = System.currentTimeMillis();
		cmt.setCreatetime(ct);
		cmt.setUpdatetime(ct);
		cmt.setIsdel(CommentStatus.NOMAL.getValue());
		int i = commentMapper.insert(cmt);
		if (i > 0) {
			commentService.hSet(RedisService.COMMENT_KEY, String.valueOf(cmt.getId()), cmt);
			comment4096.put(String.valueOf(cmt.getId()), cmt);
			commentIdsService.add2ListLeft(BookComment_List + cmt.getBookid(), cmt.getId());
		}
		return i;
	}

	public int updateCommentStatus(Long cmtId, CommentStatus status) {
		Comment cmt = new Comment();
		cmt.setId(cmtId);
		cmt.setIsdel(status.getValue());
		int i = commentMapper.updateByPrimaryKeySelective(cmt);
		if (i > 0 && status.getValue() < 0) {
			comment4096.invalidate(String.valueOf(cmtId));
			cmt = commentMapper.selectByPrimaryKey(cmtId);
			commentIdsService.listRemoveValue(BookComment_List + cmt.getBookid(), 1, cmtId);
		}
		return i;
	}

}
