package bot.fun.tamagotchi;

public class TriviaQuestion {
	
	private String userId;
	private String commentId;
	private int answer;
	
	
	public String getUserId() {
		return userId;
	}


	public String getCommentId() {
		return commentId;
	}


	public int getAnswer() {
		return answer;
	}


	public TriviaQuestion(String userId, String commentId, int answer){
		this.userId = userId;
		this.commentId = commentId;
		this.answer = answer;
	}
	
	
	
}
