package com.rubiks.Search;



public class CubeNode<S,A,L> extends Node<S,A>{
    private final L phaseID;
    private String path;

    public CubeNode(S state, Node<S,A> parent, A action, int pathCost,L phaseID,String path) {
        super(state, parent, action, pathCost);
        this.phaseID = phaseID;
        this.path = path;

    }
    public CubeNode(S state,L phaseID) {
        this(state, null, null, 0,phaseID,"");
    }
    public CubeNode(S state,L phaseID,String path) {
        this(state, null, null, 0,phaseID,path);
    }

    public String getPath(){
        return this.path;
    }

    public CubeNode<S,A,L> copy(){
        return new CubeNode<S, A, L>(this.getState(), this.phaseID);
    }
}
