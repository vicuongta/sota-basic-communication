package AS4.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.RobotBlackboard;
import AS4.motions.AskMotion;
import AS4.motions.CheerUpMotion;
import AS4.motions.ComfortMotion;
import AS4.motions.WaveElbowMotion;
import jp.vstone.RobotLib.CPlayWave;

public class CheckAnswer extends LeafTask<RobotBlackboard> {
    private String expectedAnswer;
    private String userAnswerQuiz;
    private String correctSpeech;
    private String comfortSpeech;
    private String wrongSpeech;
    private String answerSpeech;
    private String goodByeSpeech;

    @Override
    public Status execute() {
        // Speech paths
        correctSpeech = getObject().quizPath + "answer/correct"; // path to correct answer sound files
        wrongSpeech = getObject().quizPath + "answer/wrong"; // path to wrong answer sound files
        comfortSpeech = getObject().quizPath + "answer/comfort"; // path to wrong answer sound files
        answerSpeech = getObject().quizPath + "answer/"; // path to answer sound files

        goodByeSpeech = getObject().greetPath + "thank_you.wav";

        // Get the expected answer and user answer from the blackboard
        expectedAnswer = getObject().expectedAnswer.trim(); // get the expected answer from the blackboard
        userAnswerQuiz = getObject().userAnswer.trim(); // get the user answer from the blackboard

        try {
            getObject().questionAsked++;
            int index = (int)(Math.random() * 3 + 1); // random complement sound
            String end = String.valueOf(index) + ".wav"; // 1.wav, 2.wav, 3.wav
            if (userAnswerQuiz.equalsIgnoreCase(expectedAnswer)) {
                CheerUpMotion cheer = new CheerUpMotion(correctSpeech + end, answerSpeech + expectedAnswer.toLowerCase() + ".wav");
                cheer.run();
            }
            else {
                ComfortMotion comfort = new ComfortMotion(comfortSpeech + end, wrongSpeech + end, answerSpeech + expectedAnswer.toLowerCase() + ".wav");
                comfort.run();
            }
            if (getObject().questionAsked < getObject().totalQuestions) {
                int index1 = (int)(Math.random() * 2 + 1);
                String end2 = String.valueOf(index1) + ".wav";
                CPlayWave.PlayWave_wait(getObject().quizPath + "questions/next_question" + end2); // play the next question sound file 1 or 2
            }
            if (getObject().questionAsked == getObject().totalQuestions) {
                WaveElbowMotion bye = new WaveElbowMotion(goodByeSpeech);
                bye.run();
                getObject().interactionDone = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Status.FAILED; // Ensure a return value in case of an exception
        }
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new CheckAnswer();
    }
}
