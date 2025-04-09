package AS4.behaviors;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;

import AS4.conditions.HasFaceDetected;
import AS4.tasks.DetectFaceFromMotion;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.camera.CRoboCamera;
import AS4.RobotBlackboard;

public class DetectFaceBehavior {

    private static CRoboCamera cam;

    // Initialize camera and motion here safely
    static {
        try {
            CRobotMem mem = new CRobotMem();
            CSotaMotion motion = new CSotaMotion(mem);
            if (mem.Connect()) {
                motion.InitRobot_Sota();
                cam = new CRoboCamera("/dev/video0", motion);
                CRobotUtil.wait(1000); // Give time to stabilize
            } else {
                System.err.println("Failed to connect to Sota memory!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BehaviorTree<RobotBlackboard> createDetectFaceTree(RobotBlackboard bb) {
        @SuppressWarnings("unchecked")  // just to suppress the annoying message
        Selector<RobotBlackboard> selector = new Selector<>(
            new HasFaceDetected(),
            new DetectFaceFromMotion(cam)
        );

        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<>(selector);
        tree.setObject(bb);
        return tree;
    }
}
