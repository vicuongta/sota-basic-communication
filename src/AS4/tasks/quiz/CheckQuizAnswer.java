package AS4.tasks.quiz;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import AS4.RobotBlackboard;
import marytts.LocalMaryInterface;
import marytts.util.data.audio.AudioPlayer;

public class CheckQuizAnswer extends LeafTask<RobotBlackboard> {
    private final String correct;

    public CheckQuizAnswer(String correct) {
        this.correct = correct;
    }

    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        try {
            LocalMaryInterface mary = new LocalMaryInterface();
            AudioPlayer ap = new AudioPlayer();
            String response;
            if (bb.lastKeyword.equalsIgnoreCase(correct)) {
                response = "Correct! You're doing great!";
            } else {
                response = "That's incorrect. The correct answer was " + correct.toUpperCase();
            }
            ap.setAudio(mary.generateAudio(response));
            ap.start();
            ap.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new CheckQuizAnswer(correct);
    }
}
