package AS4.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import AS4.RobotBlackboard;
import jp.vstone.RobotLib.CPlayWave;

public class CheckQuizAnswer extends LeafTask<RobotBlackboard> {
    private String expectedAnswer;
    private String userAnswerQuiz;
    private String correctSpeech;
    private String mistakeSpeech;
    private String wrongSpeech;
    private String answerSpeech;

    @Override
    public Status execute() {
        // Speech paths
        correctSpeech = getObject().quizPath + "answer/correct"; // path to correct answer sound files
        wrongSpeech = getObject().quizPath + "answer/wrong"; // path to wrong answer sound files
        mistakeSpeech = getObject().quizPath + "answer/mistake"; // path to wrong answer sound files
        answerSpeech = getObject().quizPath + "answer/"; // path to answer sound files

        // Get the expected answer and user answer from the blackboard
        expectedAnswer = getObject().expectedAnswer.trim(); // get the expected answer from the blackboard
        userAnswerQuiz = getObject().userAnswerQuiz.trim(); // get the user answer from the blackboard

        // System.out.println("Expected answer: " + expectedAnswer); // print the expected answer
        // System.out.println("User answer: " + userAnswerQuiz); // print the user answer
        // System.out.println(userAnswerQuiz.equalsIgnoreCase(expectedAnswer));
        try {
            int index = (int)(Math.random() * 3 + 1); // random complement sound
            String end = String.valueOf(index) + ".wav"; // 1.wav, 2.wav, 3.wav
            if (userAnswerQuiz.equalsIgnoreCase(expectedAnswer)) {
                CPlayWave.PlayWave_wait(correctSpeech + end);
            }
            else {
                CPlayWave.PlayWave_wait(mistakeSpeech + end); // play the first sound file for wrong answer
                CPlayWave.PlayWave_wait(wrongSpeech + end);
                CPlayWave.PlayWave_wait(answerSpeech + expectedAnswer.toLowerCase() + ".wav"); // play the second sound file for wrong answer
                System.out.println("Playing:" + answerSpeech + expectedAnswer.toLowerCase() + ".wav");
            }
            CPlayWave.PlayWave_wait(getObject().quizPath + "/questions/next_question" + end); // play the next question sound file 1 or 2
        } catch (Exception e) {
            e.printStackTrace();
            return Status.FAILED; // Ensure a return value in case of an exception
        }
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<RobotBlackboard> copyTo(Task<RobotBlackboard> task) {
        return new CheckQuizAnswer();
    }
}
