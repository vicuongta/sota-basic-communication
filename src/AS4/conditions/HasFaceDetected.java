package AS4.conditions;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.RobotBlackboard;

public class HasFaceDetected extends LeafTask<RobotBlackboard> {


    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();
        if (bb.motionDetected) {           
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
