package AS4.conditions;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.RobotBlackboard;
import AS4.motions.GreetingMotion;
import AS4.motions.QuizTimeMotion;
import jp.vstone.RobotLib.CRobotUtil;

public class HasFaceDetected extends LeafTask<RobotBlackboard> {
    final String GREET_SOUND = "greeting.wav";
    final String START_QUIZ_SOUND = "start_quiz.wav";
    
    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        if (bb.motionDetected) {
            System.out.println("Motion Detected!");
            GreetingMotion greet = new GreetingMotion(bb.greetPath + GREET_SOUND);
            greet.run();
            CRobotUtil.wait(5000);
            QuizTimeMotion quizTime = new QuizTimeMotion(bb.greetPath + START_QUIZ_SOUND);
            quizTime.run();
            CRobotUtil.wait(3000);
            return Status.SUCCEEDED;
        }
        else {
            return Status.FAILED;
        }
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new HasFaceDetected();
    }
}
