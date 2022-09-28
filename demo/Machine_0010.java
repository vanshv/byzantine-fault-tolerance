package demo;

import common.Location;
import common.Machine;

import java.util.Random;
import java.util.ArrayList;

public class Machine_demo extends Machine {
    //0 is left, 1 is right
	public Machine_demo() {
		id = nextId++;
	}

    @Override
	public void setMachines(ArrayList<Machine> machines){
        for(int i = 0; i<machines.size(); i++){
            all_machines.add(machines.get(i));
        }
        numFaulty = machines.size()/3;
    }

    @Override
    public void setStepSize(int stepSize) {
        step = stepSize;
    }

    @Override
    public void setState(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    @Override
    public void setLeader() {
        isLeader = true;
    }

    @Override
    public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
        if(phaseNum != phase){
            all_messages.clear();
            leftTally = 0;
            rightTally = 0;
            flag0 = true;
            flag1 = true;
            flag2 = true;
        }
        String str = Integer.toString(decision);
        all_messages.add(str);
        round = roundNum;
        phase = phaseNum;

        //0 is left, 1 is right
        if(decision == 1){
            rightTally++;
        }
        else{
            leftTally++;
        }

        if((round == 0) && (all_messages.size() > 0) && (flag0)){
            flag0 = false;
            doround1();
        }
        else if((round == 1) && (all_messages.size() > 2*numFaulty) && flag1){
            flag1 = false;
            doround2();
        }
        else if(round == 2){
            if(((rightTally > 2*numFaulty) || (leftTally > 2*numFaulty)) && flag2){
                flag2 = false;
                setDecision();
                turn();
                isLeader = false;
            }
        }
    }

    private void setDecision(){
        //0 is left, 1 is right
        if(isCorrect){
            if(rightTally > leftTally){
                m_decision = 1;
            }
            else{
                m_decision = 0;
            }
        }
        else{
            Random rand = new Random();
            int temp = rand.nextInt(2);
            m_decision = temp;
        }
    }

    private void doround1(){
        setDecision();
        for(int i = 0; i<all_machines.size(); i++){
            if(isCorrect){
                all_machines.get(i).sendMessage(id, phase, 1, m_decision);
            }
            else{
                Random rand = new Random();
                int temp = rand.nextInt(2);
                if(temp == 1){
                    all_machines.get(i).sendMessage(id, phase, 1, m_decision);
                }
            }
        }
    }

    private void doround2(){
        setDecision();
        for(int i = 0; i<all_machines.size(); i++){
            if(isCorrect){
                all_machines.get(i).sendMessage(id, phase, 2, m_decision);
            }
            else{
                Random rand = new Random();
                int temp = rand.nextInt(2);
                if(temp == 1){
                    all_machines.get(i).sendMessage(id, phase, 2, m_decision);
                }
            }
        }
    }

    private void turn(){
        //0 is left, 1 is right
        if(m_decision == 1){
            if(dir.getX() == 0 && dir.getY() == 1){
                dir.setLoc(-1, 0);
            }
            else if(dir.getX() == 0 && dir.getY() == -1){
                dir.setLoc(1, 0);
            }
            else if(dir.getX() == -1 && dir.getY() == 0){
                dir.setLoc(0, -1);
            }
            else if(dir.getX() == 1 && dir.getY() == 0){
                dir.setLoc(0, 1);
            }
        }
        else if(m_decision == 0){
            if(dir.getX() == 0 && dir.getY() == 1){
                dir.setLoc(1, 0);
            }
            else if(dir.getX() == 0 && dir.getY() == -1){
                dir.setLoc(-1, 0);
            }
            else if(dir.getX() == 1 && dir.getX() == 0){
                dir.setLoc(0, -1);
            }
            else if(dir.getY() == -1 && dir.getX() == 0){
                dir.setLoc(0, 1);
            }
        }
    }

    @Override
    public void move() {
        pos.setLoc(pos.getX() + dir.getX()*step, pos.getY() + dir.getY()*step);
    }

    @Override
    public String name() {
        return "demo_"+id;
    }

    @Override
    public Location getPosition() {
        return new Location(pos.getX(), pos.getY());
    }

    private boolean flag0 = true;
    private boolean flag1 = true;
    private boolean flag2 = true;
    private int numFaulty;
    private int phase = 0;
    private int round = 0;
    private int m_decision;
    private int rightTally, leftTally;
    private ArrayList<String> all_messages = new ArrayList<String>();
    private ArrayList<Machine> all_machines = new ArrayList<Machine>();
    private int step;
    private Location pos = new Location(0,0);
    private Location dir = new Location(1,0); // using Location as a 2d vector. Bad!
    private static int nextId = 0;
    private int id;
    private boolean isCorrect;
    private boolean isLeader = false;
}
