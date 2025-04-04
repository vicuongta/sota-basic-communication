package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class HasAnswerDetected extends LeafTask <RobotBlackboard> {
    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        if (bb.hasReceivedAnswer) {
            return Status.SUCCEEDED;
        } else {
            return Status.FAILED;
        }
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new HasAnswerDetected();
    }
}
