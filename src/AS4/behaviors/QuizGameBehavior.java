package AS4.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Sequence;

import AS4.RobotBlackboard;
import AS4.tasks.AskQuestions;
import AS4.tasks.CheckAnswer;
import AS4.tasks.ListenKeyword;

public class QuizGameBehavior {
    private static String filePath = "/home/root/sotaprograms/resources/sound/a4-sound/quiz/questions/";

    public static class QuizEntry {
        public String wavPath;
        public String answer;

        public QuizEntry(String wavPath, String answer) {
            this.wavPath = wavPath;
            this.answer = answer;
        }
    }

    public static BehaviorTree<RobotBlackboard> createQuizTree(RobotBlackboard bb) {
        List<QuizEntry> allQuestions = new ArrayList<>();
        allQuestions.add(new QuizEntry("q1.wav", "Apple"));
        allQuestions.add(new QuizEntry("q2.wav", "Banana"));
        allQuestions.add(new QuizEntry("q3.wav", "Summer"));
        allQuestions.add(new QuizEntry("q4.wav", "Purple"));
        allQuestions.add(new QuizEntry("q5.wav", "Dog"));
        allQuestions.add(new QuizEntry("q6.wav", "Water"));
        allQuestions.add(new QuizEntry("q7.wav", "Blue"));
        allQuestions.add(new QuizEntry("q8.wav", "Paris"));
        allQuestions.add(new QuizEntry("q9.wav", "Mars"));
        allQuestions.add(new QuizEntry("q10.wav", "Four"));


        Collections.shuffle(allQuestions); // randomize 5 questions
        List<QuizEntry> selectedQuestions = allQuestions.subList(0, 2);
        bb.totalQuestions = selectedQuestions.size(); // set the number of questions to ask
        
        // Create the behavior tree sequence
        Sequence<RobotBlackboard> sequence = new Sequence<>();
        for (QuizEntry entry : selectedQuestions) {
            sequence.addChild(new AskQuestions(filePath + entry.wavPath, entry.answer));
            sequence.addChild(new ListenKeyword());
            sequence.addChild(new CheckAnswer());
        }

        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<>(sequence);
        tree.setObject(bb);
        return tree;
    }
} 
