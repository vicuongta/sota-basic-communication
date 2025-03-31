package AS4.tasks.quiz;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.RobotBlackboard;
import marytts.LocalMaryInterface;
import marytts.util.data.audio.AudioPlayer;

public class AskQuestionQuiz extends LeafTask<RobotBlackboard> {
    private final String question;
    private final String correct;

    public AskQuestionQuiz(String question, String correct) {
        this.question = question;
        this.correct = correct;
    }

    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        bb.lastKeyword = "";
        bb.keywordDetected = false;
        bb.expectedAnswer = correct;

        try {
            LocalMaryInterface mary = new LocalMaryInterface();
            AudioPlayer ap = new AudioPlayer();
            ap.setAudio(mary.generateAudio(question));
            ap.start();
            ap.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new AskQuestionQuiz(question, correct);
    }
}
