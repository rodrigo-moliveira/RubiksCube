package src.Search;


/*Thisletwaite algorithm implements a Breath First Search for generating the Database files. To save memory, I simplify
the general Node class (since less attributes are needed).
* */

public class ThisletwaiteNode<S,L> {

    // n.STATE: the state in the state space to which the node corresponds;
    private final S state;

    //phaseID: the ID of the current Thisletwaite Node, for the given state of the cube (depends on the phase)
    private final L phaseID;

    // path: sequence of moves to generate the current state (from the solved position)
    private final String path;



    public ThisletwaiteNode(S state, L phaseID, String path) {
        this.state = state;
        this.phaseID = phaseID;
        this.path = path;
    }

    public ThisletwaiteNode(S state,L phaseID) {
        this(state, phaseID,"");
    }

    public S getState() {
        return state;
    }


    @Override
    public String toString() {
        return "[state=" + getState() + ", phase ID=" + phaseID + ", path=" + path + "]";
    }



    public String getPath(){
        return this.path;
    }

    public ThisletwaiteNode<S,L> copy(){
        return new ThisletwaiteNode<>(this.getState(), this.phaseID);
    }
}
