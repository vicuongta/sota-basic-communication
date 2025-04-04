package AS4;

public class RobotBlackboard {
     // General variables
     public String filePath = "/home/root/sotaprograms/resources/sound/a4-sound/"; // path to sound files

    // Quiz variables
    public int totalQuestions = 0; 
    public String expectedAnswer = ""; 
    public String userAnswerQuiz = ""; // used for user answer in quiz mode
    public String quizPath = filePath + "quiz/"; // path to quiz files
    public boolean hasReceivedAnswer = false; // used to check if the user has answered the quiz question

    // Chat variables
    public String userEmotion = ""; // "happy", "sad", or "" when user hasn't answered
    public String userAnswerChat = ""; // used for user answer in chat mode
    public String chatPath = filePath + "chat/"; // path to quiz files

    // Idle state variables
    public String mode = ""; // this code be "chat" or "quiz"
    public boolean faceDeteced = false;
}
