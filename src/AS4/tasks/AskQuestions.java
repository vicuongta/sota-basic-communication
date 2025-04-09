package AS4.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.RobotBlackboard;
import AS4.motions.AskMotion;

public class AskQuestions extends LeafTask<RobotBlackboard> {
    private String question;
    private String correct;
    private String path; // path to the question sound files

    public AskQuestions(String question, String correct) {
        this.question = question;
        this.correct = correct;
    }

    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        bb.expectedAnswer = correct; // set the expected answer
        path = bb.quizPath + "questions/"; // path to the question sound files

        try {  
            AskMotion ask = new AskMotion(question, path+"Qphrase.wav");
            ask.run();
        }  catch (Exception e) {
            e.printStackTrace();
            return Status.FAILED;
        }
        return Status.SUCCEEDED;
    }
    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new AskQuestions(question, correct);
    }
}
