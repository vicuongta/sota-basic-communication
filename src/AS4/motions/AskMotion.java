package AS4.motions;

import java.awt.Color;

import jp.vstone.RobotLib.*;

public class AskMotion {
    static final String RESOURCES = "../resources/";
    static final String SOUNDS = RESOURCES + "sound/a4-sound/mode/quiz_mode";
    String question;
    String list;
    String askAnswer;

    public AskMotion(String question, String list, String askAnswer) {
        this.question = question;
        this.list = list;
        this.askAnswer = askAnswer;
    }

    public void run() {
        CRobotPose pose = new CRobotPose(); // classes to manage robot pose information
        CRobotMem mem = new CRobotMem(); // connector for the Sota's information system (VSMD), connects via internal
                                         // socket.
        CSotaMotion motion = new CSotaMotion(mem);

        if (mem.Connect()) {
            motion.InitRobot_Sota(); // initialize the Sota VSMD
            motion.ServoOn();

            pose.setLED_Sota(Color.WHITE, Color.WHITE, 255, Color.WHITE);
            motion.play(pose, 1000);

            // Ask question
            CPlayWave.PlayWave(this.question);
            pose = new CRobotPose();
            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                    new Short[] { 0, -543, -687, -169, 793, -79, -58, 174 }); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll(); // also async public boolean isEndInterpAll()
            CRobotUtil.wait(5000);

            CPlayWave.PlayWave(this.list);
            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                    new Short[] { 0, -685, -773, 130, 657, -399, -3, 12 }); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();
            CRobotUtil.wait(500); // pause the program / current thread

            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                    new Short[] { 0, -158, -775, 576, 661, 300, 107, 23 }); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();
            CRobotUtil.wait(500); // pause the program / current thread

            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                    new Short[] { 0, -42, -778, 50, 804, 26, -120, 23 }); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();

            CRobotUtil.wait(1000); // pause the program / current thread

            CPlayWave.PlayWave(this.askAnswer);
            // Ask for answer
            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 },
                    new Short[] { 0, -369, -82, 425, 21, -85, -114, -22 }); // initial pose
            motion.play(pose, 1000);
            motion.waitEndinterpAll();

            CRobotUtil.wait(1000); // pause the program / current thread

            motion.ServoOff();
            // pose = new CRobotPose();
            // pose.setLED_Sota(Color.BLACK, Color.BLACK, 0, Color.BLACK);
            // motion.play(pose, 500);
            motion.waitEndinterpAll();
        }
    }
}
