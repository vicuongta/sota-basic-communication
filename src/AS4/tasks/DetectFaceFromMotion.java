package AS4.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.RobotBlackboard;
import AS4.motions.WaveElbowMotion;
import AS4.motions.QuizTimeMotion;
import jp.vstone.RobotLib.*;
import jp.vstone.camera.*;

import java.time.Duration;
import java.time.Instant;

public class DetectFaceFromMotion extends LeafTask<RobotBlackboard> {
    private static final int MOTION_DETECT_TIME_SECONDS = 10;
    private static final double MOTION_THRESHOLD = 0.02;
    private static final int FACE_DETECT_TIME_SECONDS = 3;

    final String GREET_SOUND = "greeting.wav";
    final String START_QUIZ_SOUND = "start_quiz.wav";

    private CRoboCamera cam;

    private boolean motionPhase = true; // true = detecting motion, false = detecting face
    private boolean started = false;

    private Instant startTime;

    public DetectFaceFromMotion(CRoboCamera cam) {
        this.cam = cam;
    }

    @Override
    public Status execute() {
        RobotBlackboard bb = getObject();

        if (!started) {
            if (motionPhase) {
                System.out.println("Starting motion detection...");
                cam.StartMotionDetection();
                CRobotUtil.wait(500);
            } else {
                System.out.println("Starting face detection...");
                cam.StartFaceDetect();
                CRobotUtil.wait(1000);
            }
            startTime = Instant.now();
            started = true;
        }

        if (motionPhase) {
            double motionResult = cam.getMotionDetectResult();
            System.out.println("Motion detection result: " + motionResult);

            if (motionResult > MOTION_THRESHOLD) {
                System.out.println("Motion detected!");
                bb.motionDetected = true;
                cam.StopMotionDetection();

                // Switch to face detection phase
                motionPhase = false;
                started = false;
                return Status.RUNNING;
            }

            if (Duration.between(startTime, Instant.now()).getSeconds() >= MOTION_DETECT_TIME_SECONDS) {
                System.out.println("Motion not detected in time.");
                cam.StopMotionDetection();
                return Status.FAILED;
            }

            return Status.RUNNING;
        } else {
            FaceDetectResult result = cam.getDetectResult();
            if (result.isDetect() && result.getFaceNum() > 0) {
                System.out.println("Face detected!");
                bb.faceDetected = true;
                cam.StopFaceDetect();
                return Status.SUCCEEDED;
            }

            // We decided to move on from this since face detection is not working properly
            // Camera test works but some how face is not detected
            if (Duration.between(startTime, Instant.now()).getSeconds() >= FACE_DETECT_TIME_SECONDS) {
                System.out.println("Face not detected in time.");
                cam.StopFaceDetect();
                WaveElbowMotion greet = new WaveElbowMotion(bb.greetPath + GREET_SOUND);
                greet.run();
                CRobotUtil.wait(7000);
                QuizTimeMotion quizTime = new QuizTimeMotion(bb.greetPath + START_QUIZ_SOUND);
                quizTime.run();
                CRobotUtil.wait(3000);
                // If face is not detected, it should return FAILED but since face detection is not working, we let it pass through
                return Status.SUCCEEDED; 
            }

            return Status.RUNNING;
        }
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new DetectFaceFromMotion(cam);
    }
}
