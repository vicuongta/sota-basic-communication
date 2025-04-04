package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class SubTreeTask<T> extends LeafTask<T> {
    private BehaviorTree<T> subtree;

    public SubTreeTask(BehaviorTree<T> subtree) {
        this.subtree = subtree;
    }

    @Override
    public Status execute() {
        subtree.step();
        return subtree.getStatus();
    }

    @Override
    protected Task<T> copyTo(Task<T> task) {
        return new SubTreeTask<>(subtree);
    }
}
