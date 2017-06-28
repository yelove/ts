package com.ts.main.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ts.main.bean.model.Comment;
import com.ts.main.bean.model.CommentZan;
import com.ts.main.bean.model.User;
import com.ts.main.bean.vo.CommentDto;
import com.ts.main.bean.vo.CommentVo;
import com.ts.main.common.CommentStatus;
import com.ts.main.mapper.CommentMapper;
import com.ts.main.mapper.CommentZanMapper;
import com.ts.main.utils.TimeUtils4book;

@Service
public class CommentService {

	@Autowired
	private CommentMapper commentMapper;

	@Autowired
	private UserService userService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private CommentZanMapper commentZanMapper;

	@Autowired
	RedisService<Long> commentIdsService;

	@Autowired
	RedisService<Comment> commentService;
	

	/**
	 * 个人日记前缀
	 */
	public static final String BookComment_List = "BkCmt_List_";


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
				lis.add(cmt.getId());
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
		dto.setId(cmt.getId());
		dto.setBookid(cmt.getBookid());
		dto.setComment(cmt.getComment());
		dto.setCreatetime(cmt.getCreatetime());
		dto.setCreateTimeStr(TimeUtils4book.long2Str(cmt.getCreatetime()));
		dto.setIsdel(cmt.getIsdel());
		dto.setUserid(cmt.getUserid());
		User user = userService.getUserBiIdWithCache(cmt.getUserid());
		dto.setUserimg(user.getImgurl());
		dto.setUsername(StringUtils.isEmpty(user.getName())?user.getTsno().toString():user.getName());
		dto.setZan(new Long(getCommentZanSize(cmt.getId())));
		return dto;
	}

	public Comment getByid(final Long id) {
		final String key = String.valueOf(id);
		Comment cmt = commentService.hGet(RedisService.COMMENT_KEY, key);
		if (null == cmt) {
			cmt = commentMapper.selectByPrimaryKey(id);
			if (null != cmt) {
				commentService.hSet(RedisService.COMMENT_KEY, key, cmt);
			}
		}
		return cmt;
	}

	public int saveComment(Comment cmt) {
		long ct = System.currentTimeMillis();
		cmt.setCreatetime(ct);
		cmt.setUpdatetime(ct);
		cmt.setIsdel(CommentStatus.NOMAL.getValue());
		int i = commentMapper.insertSelective(cmt);
		bookService.updateBookCommentSize(cmt.getBookid(), true);
		if (i > 0) {
			commentService.hSet(RedisService.COMMENT_KEY, String.valueOf(cmt.getId()), cmt);
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
			cmt = commentMapper.selectByPrimaryKey(cmtId);
			commentIdsService.listRemoveValue(BookComment_List + cmt.getBookid(), 1, cmtId);
		}
		return i;
	}
	
	private static final String CMTZAN = "cmt_zan_";
	
	private Long getCommentZanSize(Long cmtid) {
		Long size =  commentIdsService.hSzie(CMTZAN+cmtid);
		return size;
	}

	public int commentZan(Long userId, Long cmtId){
		String key = CMTZAN+cmtId;
		CommentZan bzk = new CommentZan();
		bzk.setCmtid(cmtId);
		bzk.setUserid(userId);
		if(commentIdsService.hHashKey(key, String.valueOf(userId))){
			commentIdsService.hDel(key, String.valueOf(userId));
			commentZanMapper.deleteByPrimaryKey(bzk);
			return -1;
		}else{
			CommentZan cz = commentZanMapper.selectByPrimaryKey(bzk);
			if(null==cz){
				bzk.setCreatetime(System.currentTimeMillis());
				commentZanMapper.insert(bzk);
				return 1;
			}else{
				commentZanMapper.deleteByPrimaryKey(bzk);
				//初始化
				List<Long> userid = commentZanMapper.selectByCommentId(bzk.getCmtid());
				Map<String,Long> map = Maps.newHashMap();
				for(Long id : userid){
					map.put(String.valueOf(id), 1l);
				}
				commentIdsService.hSetAll(key, map);
				return -1;
			}
		}
	}

}
