package AS4;

public class RobotBlackboard {
     // General variables
     public String filePath = "/home/root/sotaprograms/resources/sound/a4-sound/"; // path to sound files

    // Quiz variables
    public int totalQuestions; 
    public String expectedAnswer = ""; 
    public String userAnswer = ""; // used for user answer in quiz mode
    public String quizPath = filePath + "quiz/"; // path to quiz files
    public boolean hasReceivedAnswer = false; // used to check if the user has answered the quiz question
    public int questionAsked = 0;

    // Idle state variables
    public boolean faceDetected = false;
    public boolean motionDetected = false;
    public String greetPath = filePath + "greet/";
}
