package AS4;

public class RobotBlackboard {
    public boolean faceDeteced = false;
    public String lastKeyword = "";
    public boolean keywordDetected = false;
    public boolean hasGreeted = false;
    public String mode = "idle"; // this code be "chat" or "quiz"
    public String expectedAnswer = "";
}
