package com.rubiks.cube;

// Node class for Search algorithm. Here, it is used in a Breath First Search in order to generate the 4 databases
// of the Thisletwaite method
public class Node<S,L> implements Cloneable{

    // S state: the state in the state space to which the node corresponds;
    private final S state;

    // L phaseID: the ID of the current Node
    private final L phaseID;

    // path: sequence of moves to generate the current state
    private final String path;

    public Node(S state, L phaseID, String path) {
        this.state = state;
        this.phaseID = phaseID;
        this.path = path;
    }

    public Node(S state,L phaseID) {
        this(state, phaseID,"");
    }

    public S getState() {
        return state;
    }
    
    public L getPhaseID()
    {
    	return phaseID;
    }


    @Override
    public String toString() {
        return "[state=" + getState() + ", phase ID=" + getPhaseID() + ", path=" + path + "]";
    }

    public String getPath(){
        return this.path;
    }
    
    @Override
    public Node<S,L> clone() {
    	return new Node<S, L>(this.getState(), this.phaseID);
    }
}
