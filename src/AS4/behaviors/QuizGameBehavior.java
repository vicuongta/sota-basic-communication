package AS4.behaviors;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Sequence;

import AS4.RobotBlackboard;

import AS4.tasks.quiz.AskQuestionQuiz;
import AS4.tasks.quiz.ListenKeywordQuiz;
import AS4.tasks.quiz.CheckQuizAnswer;

public class QuizGameBehavior {

    public static BehaviorTree<RobotBlackboard> createQuizTree(RobotBlackboard bb) {
        @SuppressWarnings("unchecked")  // just to suppress the annoying message
        BehaviorTree<RobotBlackboard> tree = new BehaviorTree<RobotBlackboard>(
            new Sequence<RobotBlackboard>(
                new AskQuestionQuiz("What is 2 + 2? A: 3  B: 4  C: 100", "b"),
                new ListenKeywordQuiz(),
                new CheckQuizAnswer("b"),

                new AskQuestionQuiz("Which planet is known as the Red Planet? A: Mars  B: Venus", "a"),
                new ListenKeywordQuiz(),
                new CheckQuizAnswer("a"),

                new AskQuestionQuiz("What is the capital of France? A: Rome  B: Paris", "b"),
                new ListenKeywordQuiz(),
                new CheckQuizAnswer("b")
            )
        );
        tree.setObject(bb);
        return tree;
    }
} 
