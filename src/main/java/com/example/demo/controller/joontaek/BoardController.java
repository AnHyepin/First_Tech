package com.example.demo.controller.joontaek;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.BoardDto;
import com.example.demo.dto.BoardViewDto;
import com.example.demo.dto.CommentDto;
import com.example.demo.service.IBoardService;
import com.example.demo.service.ICommentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/b")
public class BoardController {

	@Value("${app.page.amount}")
	private int amount = 5;

	@Autowired
	IBoardService boardService;

	@Autowired
	ICommentService commentService;

	@RequestMapping("/board/{pageNum}")
	public String board(@PathVariable("pageNum") int pageNum, Model model) {

		// 여기부터
		int startNum = pageNum * amount - amount;
		List<BoardViewDto> boardList = boardService.getBoardListPaging(startNum, amount);
		int totalCnt = boardService.getBoardCount();
		int endPageNum = Math.ceilDiv(totalCnt, amount);
		model.addAttribute("boardList", boardList);
		model.addAttribute("currentPageNum", pageNum);
		model.addAttribute("endPageNum", endPageNum);
		// 여기까지 게시판 목록


		return "/taek/board";
	}

	@RequestMapping("/boardDetail/{boardNum}")
	public String boardDetail(@PathVariable("boardNum") int boardNum, Model model) {

		BoardDto board = boardService.getBoard(boardNum);
		board.setViews(board.getViews() + 1); // 조회수 +1
		boardService.BoardViews(board.getViews(), boardNum);

		model.addAttribute("board", board);
		List<CommentDto> commentList = commentService.getCommentList(boardNum);

		model.addAttribute("commentList", commentList);

		return "/taek/boardDetail";
	}

	@RequestMapping("boardWriteForm")
	public String boardWriteForm(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("boardUser", "텐타숑");//나중에 삭제할거 임시 세션저장

		return "/taek/boardWriteForm";
	}

	@RequestMapping("boardWrite")
	public String boardWrite(BoardDto board, Model model,HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userId = (String)session.getAttribute("boardUser");
		board.setBoardWriter(userId);
		boardService.regBoard(board);
		// 여기부터
		int pageNum = 1;
		int startNum = pageNum * amount - amount;
		List<BoardViewDto> boardList = boardService.getBoardListPaging(startNum, amount);
		int totalCnt = boardService.getBoardCount();
		int endPageNum = Math.ceilDiv(totalCnt, amount);
		// 댓글 수 뽑아오기
		model.addAttribute("boardList", boardList);
		model.addAttribute("currentPageNum", pageNum);
		model.addAttribute("endPageNum", endPageNum);
		
		// 여기까지 게시판 목록


		return "/taek/board";
	}

	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓코멘트 관리↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	@RequestMapping("addComment")
	@ResponseBody
	public List<CommentDto> addComment(@RequestParam("content") String content, @RequestParam("boardNum") int boardNum,
			HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		session.setAttribute("userId", "박준택");
		String userId = (String) session.getAttribute("userId");
		// 임시로 세션에 아이디 저장

		commentService.regComment(content, boardNum, userId);

		List<CommentDto> commentList = commentService.getCommentList(boardNum);
		model.addAttribute("comment", commentList);

		return commentService.getCommentList(boardNum);
	}

	@RequestMapping("getCommentList")
	@ResponseBody
	public List<CommentDto> getCommentList(@RequestParam("boardNum") int boardNum) {
		return commentService.getCommentList(boardNum);
	}

}
