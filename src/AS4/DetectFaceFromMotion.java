package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import jp.vstone.RobotLib.*;
import jp.vstone.camera.*;

import java.time.Duration;
import java.time.Instant;

// Sota will detect motion and then try to detect face
// Face Detection is not working properly so we assume 
// user would show up in front of camera to trigger motion detect
public class DetectFaceFromMotion extends LeafTask<RobotBlackboard> {
    private final int MOTION_DETECT_TIME_SECONDS = 10; // try to detect motion in 10s
    private final double MOTION_THRESHOLD = 0.03; // react to real motion
    private final int FACE_DETECT_TIME_SECONDS = 5; // try to detect face in 5s
    private CRoboCamera cam;

    public DetectFaceFromMotion(CRoboCamera cam) {
        this.cam = cam;
    }

    @Override
    public Status execute() {
        cam.StartMotionDetection();
        CRobotUtil.wait(500); // to stabilize

        RobotBlackboard bb = getObject();
        Instant motionStartTime = Instant.now();

        // First try to detect (real) motion
        while (Duration.between(motionStartTime, Instant.now()).getSeconds() < MOTION_DETECT_TIME_SECONDS) {
            double motionResult = cam.getMotionDetectResult();
            System.out.println("Motion detection result: " + motionResult);

            if (motionResult > MOTION_THRESHOLD) {
                System.out.println("Motion detected!");
                bb.motionDetected = true;
                break; // exit loop when motion detected
            }
            CRobotUtil.wait(500); // check every 0.5 second
            return Status.RUNNING;
        }

        cam.StopMotionDetection();

        // Now try detecting face
        if (bb.motionDetected) {
            System.out.println("Starting face detection...");
            cam.StartFaceDetect();
            CRobotUtil.wait(1000); // Stabilize

            Instant faceStartTime = Instant.now();

            while (Duration.between(faceStartTime, Instant.now()).getSeconds() < FACE_DETECT_TIME_SECONDS) {
                FaceDetectResult result = cam.getDetectResult();
                if (result.isDetect()) {
                    System.out.println("Face detected!");
                    bb.faceDetected = true;
                    break;
                }

                CRobotUtil.wait(500);

                return Status.RUNNING;
            }

            cam.StopFaceDetect();

            if (!bb.faceDetected) {
                System.out.println("No face detected after motion.");
            }
        }
        else {
            System.out.println("No motion detected during the period.");
            return Status.FAILED;
        }

        return Status.SUCCEEDED;
    }    

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new DetectFaceFromMotion(cam);
    }
}
