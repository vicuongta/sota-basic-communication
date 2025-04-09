package AS4; // Replace with your real package

import java.time.Duration;
import java.time.Instant;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.FaceDetectResult;

public class MotionTriggerFaceDetect {

    public static void runMotionThenFaceDetection(CRoboCamera cam) {
        final int MOTION_DETECT_TIME_SECONDS = 10;
        final double MOTION_THRESHOLD = 0.02; // Only react to real motion
        final int FACE_DETECT_TIME_SECONDS = 5;

        // --- Start Motion Detection ---
        System.out.println("Starting motion detection...");
        cam.StartMotionDetection();
        CRobotUtil.wait(500); // Stabilize

        boolean motionDetected = false;
        Instant motionStartTime = Instant.now();

        while (Duration.between(motionStartTime, Instant.now()).getSeconds() < MOTION_DETECT_TIME_SECONDS) {
            double motionResult = cam.getMotionDetectResult();
            System.out.println("Motion detection result: " + motionResult);

            if (motionResult > MOTION_THRESHOLD) {
                System.out.println("Motion detected!");
                motionDetected = true;
                break; // Exit loop if motion is detected
            }

            CRobotUtil.wait(500); // check every 0.5 second
        }

        cam.StopMotionDetection();

        if (motionDetected) {
            // --- Now Start Face Detection ---
            System.out.println("Starting face detection...");
            cam.StartFaceDetect();
            CRobotUtil.wait(1000); // Stabilize

            Instant faceStartTime = Instant.now();
            boolean faceDetected = false;

            while (Duration.between(faceStartTime, Instant.now()).getSeconds() < FACE_DETECT_TIME_SECONDS) {
                FaceDetectResult result = cam.getDetectResult();
                System.out.println("FaceDetect: isDetect=" + result.isDetect() + ", numFaces=" + result.getFaceNum());

                if (result.isDetect() && result.getFaceNum() > 0) {
                    System.out.println("Face detected!");
                    faceDetected = true;
                    break;
                }

                CRobotUtil.wait(500);
            }

            cam.StopFaceDetect();

            if (!faceDetected) {
                System.out.println("No face detected after motion.");
            }
        } else {
            System.out.println("No motion detected during the period.");
        }
    }

    public static void main(String[] args) {
        final String TAG = "MotionTriggerFaceDetect";

        CRobotUtil.Log(TAG, "Start " + TAG);

        CRobotMem mem = new CRobotMem();
        CSotaMotion motion = new CSotaMotion(mem);

        if (mem.Connect()) {
            motion.InitRobot_Sota();
            CRoboCamera cam = new CRoboCamera("/dev/video0", motion);

            runMotionThenFaceDetection(cam);
        }
    }
}
